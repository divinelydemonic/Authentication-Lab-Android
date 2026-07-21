package kr.android.authenticationlab.auth.presentation.authstate

//authentication states
sealed interface AuthState {

    /**
     * The application is determining whether
     * an authenticated session already exists.
     */
    data object CheckingSession : AuthState

    /**
     * An authentication request is currently
     * in progress.
     */
    data object Authenticating : AuthState

    /**
     * The user is signed in but must verify
     * their email address before accessing
     * the application.
     */
    data object EmailVerificationRequired : AuthState

    /**
     * The user is fully authenticated and
     * allowed to access the application.
     */
    data object Authenticated : AuthState

    /**
     * No authenticated session exists.
     */
    data object Unauthenticated : AuthState
}