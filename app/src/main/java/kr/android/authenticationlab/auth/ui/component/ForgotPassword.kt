package kr.android.authenticationlab.auth.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ForgotPassword(
    isLogin: Boolean,
    onForgotPasswordClick: () -> Unit
){

    if (isLogin){
        TextButton(
            onClick = onForgotPasswordClick
        ) {
            Text(
                text = "Forgot Password?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.8f),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }

}