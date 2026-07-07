package kr.android.authpractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kr.android.authpractice.auth.data.remote.FirebaseAuthDataSource
import kr.android.authpractice.auth.data.repository.FirebaseAuthRepository
import kr.android.authpractice.auth.presentation.event.UiEvent
import kr.android.authpractice.auth.presentation.viewmodel.AuthViewModel
import kr.android.authpractice.auth.presentation.viewmodel.AuthViewModelFactory
import kr.android.authpractice.navigation.AuthApp
import kr.android.authpractice.ui.theme.AuthPracticeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create and wire the dependencies required
        // for the authentication feature (created only once)
        val dataSource = FirebaseAuthDataSource()
        val repository = FirebaseAuthRepository(dataSource)
        val authViewModelFactory = AuthViewModelFactory(repository)

        setContent {

            val viewModel : AuthViewModel = viewModel(factory = authViewModelFactory)
            val snackBarHostState = remember { SnackbarHostState() }

            LaunchedEffect(Unit) {
                viewModel.uiEvents.collect { event ->
                    when (event){
                        is UiEvent.ShowSnackBar -> { snackBarHostState.showSnackbar(event.message) }
                    }
                }
            }

            AuthPracticeTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(snackBarHostState){ data ->
                            Snackbar(
                                modifier = Modifier.padding(bottom = 40.dp),
                                shape = RoundedCornerShape(16.dp),
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                snackbarData = data
                            )
                        }
                    }
                ) { innerPadding ->
                    AuthApp(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
