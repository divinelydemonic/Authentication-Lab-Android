package kr.android.authenticationlab.auth.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AuthScreenHeader(
    title : String,
    isForgotPassword : Boolean
){

    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        style =
            if (isForgotPassword)
                MaterialTheme.typography.displaySmall
            else MaterialTheme.typography.displayMedium,
        fontFamily = FontFamily.Monospace,
        color = MaterialTheme.colorScheme.primary
    )

}