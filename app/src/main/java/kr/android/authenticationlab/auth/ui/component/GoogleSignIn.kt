package kr.android.authenticationlab.auth.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.exceptions.GetCredentialCancellationException
import kotlinx.coroutines.launch
import kr.android.authenticationlab.auth.credential.GoogleCredentialLauncher
import kr.android.authenticationlab.auth.presentation.viewmodel.AuthViewModel

/**
 * Displays a button that initiates the Google Sign-In flow.
 *
 * Upon successful sign-in, the retrieved Google ID token is
 * forwarded to the ViewModel for Firebase authentication.
 */
@Composable
fun GoogleSignIn(
    viewModel: AuthViewModel
){

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val googleCredentialLauncher = remember { GoogleCredentialLauncher(context) }

    Button(
        onClick = {
            coroutineScope.launch {
                googleCredentialLauncher.getGoogleIdToken().fold(

                    onSuccess = { idToken -> viewModel.signInWithGoogle(idToken) },

                    onFailure = { exception ->

                        if (exception is GetCredentialCancellationException) return@fold

                        viewModel.handleGoogleSignInError(exception as Exception)
                    }
                )
            }
        },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.surface
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Text(
            text = "Continue with Google",
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.titleMedium
        )
    }

}
