package kr.android.authenticationlab.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.delay
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
import kotlin.time.Duration.Companion.milliseconds

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
     * Checks whether an authenticated user session already exists.
     * Updates the authentication state and current user
     * based on the existing authentication session.
     */
    private fun checkAuthState() {

        // Retrieve the currently authenticated user from the repository.
        val user = repository.getCurrentUser()

        if (user != null) {
            // An authenticated session exists.
            _currentUser.value = user
            _authState.value = AuthState.Authenticated
        } else {
            // No authenticated session exists.
            _currentUser.value = null
            _authState.value = AuthState.Unauthenticated
        }

    }

    /**
     * Attempts to register a new user using the provided
     * email and password.
     * Updates the authentication state and current user
     * based on the registration result.
     * Emits UI events when validation or authentication fails.
     */
    fun register(
        email: String,
        password: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {

            val trimmedEmail = email.trim()

            if (!handleValidation(AuthValidator.validateEmail(trimmedEmail)))
                return@launch

            if (!handleValidation(AuthValidator.validatePassword(password)))
                return@launch

            if (!handleValidation(AuthValidator.validateMatchingPasswords(password, confirmPassword)))
                return@launch

            _authState.value = AuthState.Authenticating

            val result = repository.register(trimmedEmail, password)

            if (result.isSuccess) {

                val user = result.getOrNull()

                if (user != null) {
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated
                } else {
                    _currentUser.value = null
                    _authState.value = AuthState.Unauthenticated
                    emitMessage("Unexpected authentication error occurred.")
                }

            } else {

                val errorMessage = getReadableErrorMessage(result.exceptionOrNull())

                _currentUser.value = null
                _authState.value = AuthState.Unauthenticated
                emitMessage(errorMessage)

            }
        }
    }

    /**
     * Attempts to authenticate the user using the
     * provided email and password.
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

            if (result.isSuccess) {

                // Retrieve the authenticated user from the successful result.
                val user = result.getOrNull()

                if (user != null) {
                    // Update the authentication state with the logged-in user.
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated
                } else {
                    // A successful result without user data is unexpected.
                    _currentUser.value = null
                    _authState.value = AuthState.Unauthenticated
                    emitMessage("Unexpected authentication error occurred.")
                }

            } else {

                // Convert the authentication exception into a user-friendly message.
                val errorMessage = getReadableErrorMessage(result.exceptionOrNull())

                // Authentication failed. Emit a user-friendly error message
                _currentUser.value = null
                _authState.value = AuthState.Unauthenticated
                emitMessage(errorMessage)

            }
        }
    }

    //forgot password
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
     * Signs the current user out and
     * resets the authentication state.
     */
    fun logout(){
        repository.logout()
        _currentUser.value = null
        _authState.value = AuthState.Unauthenticated
    }


    /**
     * Emits a one-time UI event requesting
     * the presentation layer to display
     * an error message.
     */
    private suspend fun emitMessage(message : String) {
        _uiEvents.emit(UiEvent.ShowSnackBar(message))
    }

    /**
     * Converts technical authentication exceptions
     * into user-friendly messages suitable for display.
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
     * Processes a validation result.
     * Returns true when validation succeeds.
     * Emits a snackBar message and returns false when validation fails.
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

}
