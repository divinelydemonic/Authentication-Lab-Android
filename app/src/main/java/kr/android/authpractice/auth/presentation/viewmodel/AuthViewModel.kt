package kr.android.authpractice.auth.presentation.viewmodel

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
import kr.android.authpractice.auth.data.model.UserData
import kr.android.authpractice.auth.data.repository.AuthRepository
import kr.android.authpractice.auth.presentation.authstate.AuthState
import kr.android.authpractice.auth.presentation.event.UiEvent

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
        password: String
    ){
        viewModelScope.launch {

            val trimmedEmail = email.trim()

            if (trimmedEmail.isBlank()) {
                emitErrorMessage("Email cannot be empty!")
                return@launch
            }

            if (password.isBlank()) {
                emitErrorMessage("Password cannot be empty!")
                return@launch
            }

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
                    emitErrorMessage("Unexpected authentication error occurred.")
                }

            } else {

                val errorMessage = getReadableErrorMessage(result.exceptionOrNull())

                _currentUser.value = null
                _authState.value = AuthState.Unauthenticated
                emitErrorMessage(errorMessage)

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

            // Stop the login process if the email is empty.
            if (trimmedEmail.isBlank()){
                emitErrorMessage("Email cannot be empty!")
                return@launch
            }

            // Stop the login process if the password is empty.
            if (password.isBlank()){
                emitErrorMessage("Password cannot be empty!")
                return@launch
            }

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
                    emitErrorMessage("Unexpected authentication error occurred.")
                }

            } else {

                // Convert the authentication exception into a user-friendly message.
                val errorMessage = getReadableErrorMessage(result.exceptionOrNull())

                // Authentication failed. Emit a user-friendly error message
                _currentUser.value = null
                _authState.value = AuthState.Unauthenticated
                emitErrorMessage(errorMessage)

            }
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
    private suspend fun emitErrorMessage(message : String){
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

}
