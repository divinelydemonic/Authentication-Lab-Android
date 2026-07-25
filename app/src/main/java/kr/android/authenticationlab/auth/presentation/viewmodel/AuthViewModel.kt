package kr.android.authenticationlab.auth.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.android.authenticationlab.auth.data.model.UserData
import kr.android.authenticationlab.auth.data.repository.AuthRepository
import kr.android.authenticationlab.auth.presentation.authstate.AuthState
import kr.android.authenticationlab.auth.presentation.event.UiEvent
import kr.android.authenticationlab.auth.validation.AuthValidator
import kr.android.authenticationlab.auth.validation.ValidationResult

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    // Authentication state exposed to the UI
    private val _authState = MutableStateFlow<AuthState>(AuthState.CheckingSession)
    val authState = _authState.asStateFlow()

    // Currently authenticated user
    private val _currentUser = MutableStateFlow<UserData?>(null)
    val currentUser = _currentUser.asStateFlow()

    // One-time UI events such as Snackbars and navigation actions
    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()


    // Check for an existing authenticated session when the ViewModel is created
    init { checkAuthState() }

    /**
     * Checks whether an authenticated user session
     * already exists when the ViewModel is created.
     *
     * If a session exists, verifies the user's email
     * before granting access to the authenticated area
     * of the application.
     *
     * Updates the authentication state and current user
     * based on the verification result.
     */
    private fun checkAuthState() {

        viewModelScope.launch {

            // Retrieve the currently authenticated user from the repository.
            val user = repository.getCurrentUser()

            if (user == null) {
                // No authenticated session exists.
                clearAuthenticatedUser()
                return@launch
            }

            verifyAuthenticatedUser(user)
        }

    }

    /**
     * Authenticates the user using a Google ID token.
     *
     * Since Google accounts are already email verified, a successful
     * authentication immediately updates the authentication state.
     *
     * @param idToken Google ID token obtained from Credential Manager.
     */
    fun signInWithGoogle(idToken : String){

        viewModelScope.launch {

            _authState.value = AuthState.Authenticating

            val signInResult = repository.signInWithGoogle(idToken)

            signInResult.fold(
                onSuccess = { user ->

                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated

                    emitMessage("Signed in successfully.")
                },
                onFailure = { exception ->

                    clearAuthenticatedUser()
                    emitMessage(getReadableErrorMessage(exception))
                }
            )

        }
    }

    /**
     * Handles errors that occur during the Google Sign-In flow
     * before Firebase authentication begins.
     *
     * Emits a user-friendly message describing the error without
     * modifying the current authentication state.
     *
     * @param exception The exception thrown during the Google Sign-In process.
     */
    fun handleGoogleSignInError(exception: Exception) {
        viewModelScope.launch {
            emitMessage(getReadableErrorMessage(exception))
        }
    }

    /**
     * Attempts to register a new user using the provided
     * name, email, password, and password confirmation.
     *
     * Validates the input, registers a new user account,
     * and sends an email verification link to the registered user.
     *
     * On successful registration, transitions the authentication
     * state to EmailVerificationRequired. The user is not
     * considered authenticated within the application until
     * their email has been verified.
     *
     * Emits UI events when validation or registration fails.
     */
    fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {

            val trimmedName = name.trim()
            val trimmedEmail = email.trim()

            if (!handleValidation(AuthValidator.validateName(trimmedName)))
                return@launch

            if (!handleValidation(AuthValidator.validateEmail(trimmedEmail)))
                return@launch

            if (!handleValidation(AuthValidator.validatePassword(password)))
                return@launch

            if (!handleValidation(AuthValidator.validateMatchingPasswords(password, confirmPassword)))
                return@launch

            _authState.value = AuthState.Authenticating

            val result = repository.register(trimmedName, trimmedEmail, password)

            result.fold(
                onSuccess = {
                    _currentUser.value = null
                    _authState.value = AuthState.EmailVerificationRequired
                },
                onFailure = { exception ->
                    val errorMessage = getReadableErrorMessage(exception)
                    clearAuthenticatedUser()
                    emitMessage(errorMessage)
                }
            )
        }
    }

    /**
     * Refreshes the current user's email verification status.
     *
     * If the email has been verified, the user is marked as
     * authenticated. Otherwise, the authentication state
     * remains EmailVerificationRequired.
     *
     * Emits a user-friendly message when verification has not
     * yet completed or when the verification check fails.
     */
    fun verifyEmail(){

        viewModelScope.launch{

            val result = repository.isEmailVerified()

            result.fold(
                onSuccess = { isVerified ->
                    if (isVerified) {
                        _authState.value = AuthState.Authenticated
                        _currentUser.value = repository.getCurrentUser()
                    }
                    else {
                        _authState.value = AuthState.EmailVerificationRequired
                        emitMessage("Your email has not been verified yet.")
                    }
                },
                onFailure = { exception -> emitMessage(getReadableErrorMessage(exception)) }
            )

        }

    }

    /**
     * Requests a new email verification link for the
     * currently authenticated user.
     *
     * Emits a confirmation message when the verification
     * email is sent successfully, or a user-friendly
     * error message if the request fails.
     */
    fun resendVerificationEmail(){
        viewModelScope.launch {

            val result = repository.resendVerificationEmail()

            result.fold(
                onSuccess = { emitMessage("Verification email sent successfully.") },
                onFailure = { exception -> emitMessage(getReadableErrorMessage(exception)) }
            )

        }
    }

    /**
     * Attempts to authenticate the user using the
     * provided email and password.
     *
     * After a successful sign-in, checks whether the
     * user's email address has been verified before
     * granting access to the authenticated area of
     * the application.
     *
     * Updates the authentication state and current user.
     * Emits UI events when validation or authentication fails.
     */
    fun login(
        email: String,
        password: String
    ) {

        viewModelScope.launch {

            // Normalize the email by removing leading and trailing whitespace.
            val trimmedEmail = email.trim()

            // Validate email
            if (!handleValidation(AuthValidator.validateEmail(trimmedEmail)))
                return@launch

            // Validate password
            if (!handleValidation(AuthValidator.validatePassword(password)))
                return@launch

            // Show loading state while authentication is in progress.
            _authState.value = AuthState.Authenticating

            // Request authentication from the repository.
            val result = repository.login(trimmedEmail, password)

            result.fold(
                onSuccess = { user -> verifyAuthenticatedUser(user) },
                onFailure = { exception ->

                    // Convert the authentication exception into a user-friendly message.
                    val errorMessage = getReadableErrorMessage(exception)

                    // Authentication failed. Emit a user-friendly error message
                    clearAuthenticatedUser()
                    emitMessage(errorMessage)

                }
            )
        }
    }

    /**
     * Sends a password reset email to the provided
     * email address after validating the input.
     *
     * Emits a confirmation message when the request
     * succeeds and a user-friendly error message when
     * the request fails.
     */
    fun forgotPassword(email: String) {

        viewModelScope.launch {

            val trimmedEmail = email.trim()

            if (!handleValidation(AuthValidator.validateEmail(trimmedEmail)))
                return@launch

            val result = repository.forgotPassword(trimmedEmail)

            result.fold(
                onSuccess = {
                    emitMessage("If the email is registered,\nyou'll receive password reset instructions shortly.")
                },
                onFailure = { exception -> emitMessage(getReadableErrorMessage(exception)) }
            )
        }
    }

    /**
     * Signs out the current user and clears all
     * authentication-related state held by the ViewModel.
     */
    fun logout(){
        repository.logout()
        clearAuthenticatedUser()
    }


    /**
     * Emits a one-time UI event requesting the
     * presentation layer to display a snackbar message.
     */
    private suspend fun emitMessage(message : String) {
        _uiEvents.emit(UiEvent.ShowSnackBar(message))
    }

    /**
     * Maps authentication-related exceptions to
     * user-friendly messages suitable for display.
     *
     * Unknown exceptions fall back to a generic error message.
     */
    private fun getReadableErrorMessage(
        exception: Throwable?
    ): String {
        return when (exception) {

            is FirebaseNetworkException ->
                "Please check your internet connection."

            is FirebaseAuthWeakPasswordException ->
                "Password is too weak. Try something else."

            is FirebaseAuthInvalidUserException ->
                "No account found with this email.\nSign up to get started"

            is FirebaseAuthInvalidCredentialsException ->
                "Incorrect email or password. Try again!"

            is FirebaseAuthUserCollisionException ->
                "An account with this email already exists.\nLogin or try another email."

            else ->
                "Something went wrong. Please try again."
        }
    }

    /**
     * Processes the result of an input validation.
     *
     * @return true if validation succeeds; otherwise emits
     * a snackbar message and returns false.
     */
    private suspend fun handleValidation(
        result: ValidationResult
    ) : Boolean {
        return when(result) {

            ValidationResult.Success -> true

            is ValidationResult.Failure -> {
                emitMessage(result.message)
                false
            }

        }
    }

    /**
     * Clears the current authenticated user and resets
     * the authentication state to Unauthenticated.
     */
    private fun clearAuthenticatedUser() {
        _currentUser.value = null
        _authState.value = AuthState.Unauthenticated
    }

    /**
     * Checks whether the authenticated user's email address
     * has been verified.
     *
     * Updates the authentication state and current user
     * based on the verification result.
     *
     * @param user The authenticated user returned by the repository.
     */
    private suspend fun verifyAuthenticatedUser(user: UserData) {

        val verificationResult = repository.isEmailVerified()

        verificationResult.fold(
            onSuccess = { isVerified ->
                if (isVerified) {
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated
                } else {
                    _currentUser.value = null
                    _authState.value = AuthState.EmailVerificationRequired
                    emitMessage("Please verify your email before signing in.")
                }
            },
            onFailure = { exception ->
                clearAuthenticatedUser()
                emitMessage(getReadableErrorMessage(exception))
            }
        )
    }

}
