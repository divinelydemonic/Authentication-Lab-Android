package kr.android.basicfirebaseauthentication.auth.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kr.android.basicfirebaseauthentication.auth.presentation.viewmodel.AuthViewModel

@Composable
fun HomeScreen(
    viewModel: AuthViewModel
){

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primaryContainer
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Login successful",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Welcome!",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { viewModel.logout() },
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                ),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Text(
                    text = "Logout",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }

        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun HomePreview(){ HomeScreen() }