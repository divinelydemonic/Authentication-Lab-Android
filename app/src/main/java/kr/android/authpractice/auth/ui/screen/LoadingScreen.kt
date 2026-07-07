package kr.android.authpractice.auth.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun LoadingScreen(
    message : String = "Loading"
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

            Box(
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 8.dp,
                    trackColor = MaterialTheme.colorScheme.inversePrimary,
                    strokeCap = StrokeCap.Round,
                    gapSize = 16.dp
                )

                CircularProgressIndicator(
                    modifier = Modifier.size(80.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 12.dp,
                    trackColor = MaterialTheme.colorScheme.inversePrimary,
                    strokeCap = StrokeCap.Round,
                    gapSize = 32.dp
                )
            }

            Spacer(Modifier.height(56.dp))

            LoadingTextAnimation(message = "Authenticating")

        }
    }

}

@Composable
fun LoadingTextAnimation(
    message: String
){

    var dotCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500.milliseconds)
            dotCount = (dotCount + 1) % 4
        }
    }

    Text(
        text = message + ".".repeat(dotCount),
        fontWeight = FontWeight.SemiBold,
        fontFamily = FontFamily.Monospace,
        style = MaterialTheme.typography.titleLarge
    )

}

//@Preview(showBackground = true)
//@Composable
//fun LoadingPreview(){ LoadingScreen() }