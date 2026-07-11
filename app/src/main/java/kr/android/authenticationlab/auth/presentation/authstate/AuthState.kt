package kr.android.authenticationlab.auth.presentation.authstate

//authentication states
sealed interface AuthState {

    //loading states
    data object CheckingSession : AuthState
    data object Authenticating : AuthState

    //authentication states
    data object Authenticated : AuthState
    data object Unauthenticated : AuthState

}
