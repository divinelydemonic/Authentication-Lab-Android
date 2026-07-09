package kr.android.basicfirebaseauthentication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import kr.android.basicfirebaseauthentication.auth.presentation.authstate.AuthState
import kr.android.basicfirebaseauthentication.auth.presentation.viewmodel.AuthViewModel
import kr.android.basicfirebaseauthentication.auth.ui.screen.HomeScreen
import kr.android.basicfirebaseauthentication.auth.ui.screen.LoadingScreen
import kr.android.basicfirebaseauthentication.auth.ui.screen.AuthenticationScreen

/**
 * Root composable for the authentication flow.
 * Observes the authentication state from the ViewModel
 * and displays the appropriate screen.
 */
@Composable
fun AuthApp(
    modifier: Modifier,
    viewModel: AuthViewModel
) {

    // Observe the current authentication state.
    val authState by viewModel.authState.collectAsState()

    // Display the appropriate screen based on
    // the current authentication state.
    when (authState) {

        AuthState.CheckingSession -> LoadingScreen(message = "checking session")
        AuthState.Authenticating -> LoadingScreen(message = "authenticating user")

        AuthState.Unauthenticated -> AuthenticationScreen(viewModel)
        AuthState.Authenticated -> HomeScreen(viewModel)

    }
}