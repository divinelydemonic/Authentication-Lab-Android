package kr.android.authenticationlab.auth.ui.screen

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import kr.android.authenticationlab.auth.presentation.model.AuthScreenMode
import kr.android.authenticationlab.auth.presentation.model.AuthScreenConfig
import kr.android.authenticationlab.auth.presentation.viewmodel.AuthViewModel
import kr.android.authenticationlab.auth.ui.component.AuthScreenCard
import kr.android.authenticationlab.auth.ui.component.AuthScreenFooter
import kr.android.authenticationlab.auth.ui.component.AuthScreenHeader
import kr.android.authenticationlab.auth.ui.component.ForgotPassword
import kr.android.authenticationlab.auth.validation.AuthValidator

@Composable
fun AuthenticationScreen(
    viewModel: AuthViewModel
){

    //checks login mode or registration mode
    var currentScreen by remember { mutableStateOf(AuthScreenMode.LOGIN) }

    val isLogin = currentScreen == AuthScreenMode.LOGIN
    val isRegister = currentScreen == AuthScreenMode.REGISTER
    val isForgotPassword = currentScreen == AuthScreenMode.FORGOT_PASSWORD

    //dynamically changing values according to mode
    val screenConfig = when (currentScreen) {

        AuthScreenMode.LOGIN ->
            AuthScreenConfig(
                title = "Login",
                buttonText = "Login",
                footerText = "Don't have an account?",
                actionText = "Sign Up"
            )

        AuthScreenMode.REGISTER ->
            AuthScreenConfig(
                title = "Sign Up",
                buttonText = "Sign Up",
                footerText = "Already have an account?",
                actionText = "Login"
            )

        AuthScreenMode.FORGOT_PASSWORD ->
            AuthScreenConfig(
                title = "Forgot Password",
                buttonText = "Send Email",
                footerText = "Remembered password?",
                actionText = "Login"
            )
    }

    val email = remember { mutableStateOf("") }

    val password = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }

    val confirmPassword = remember { mutableStateOf("") }
    val confirmPasswordVisible = remember { mutableStateOf(false) }

    val isButtonEnabled =
        when (currentScreen) {

            AuthScreenMode.LOGIN ->
                email.value.isNotBlank() &&
                        password.value.isNotBlank() &&
                        password.value.length >= AuthValidator.MIN_PASSWORD_LENGTH

            AuthScreenMode.REGISTER ->
                email.value.isNotBlank() &&
                        password.value.isNotBlank() &&
                        confirmPassword.value.isNotBlank() &&
                        password.value.length >= AuthValidator.MIN_PASSWORD_LENGTH

            AuthScreenMode.FORGOT_PASSWORD ->
                email.value.isNotBlank()
        }

    val focusManager = LocalFocusManager.current
    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    //-- helper functions --

    fun dismissKeyboard() {
        focusManager.clearFocus()
        softwareKeyboardController?.hide()
    }

    fun clearCredentials() {
        email.value = ""
        password.value = ""
        confirmPassword.value = ""
    }

    fun clearPasswords() {
        password.value = ""
        confirmPassword.value = ""
    }

    fun navigateTo(screen: AuthScreenMode) { currentScreen = screen }


    Surface(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures { dismissKeyboard() } },
        color = MaterialTheme.colorScheme.primaryContainer
    ){
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //header
            AuthScreenHeader(
                title = screenConfig.title,
                isForgotPassword = isForgotPassword
            )

            Spacer(Modifier.height(16.dp))

            //content card
            AuthScreenCard(
                email = email.value,
                onEmailChange = { email.value = it },

                password = password.value,
                onPasswordChange = { password.value = it.take(AuthValidator.MAX_PASSWORD_LENGTH) },

                passwordVisible = passwordVisible.value,
                onPasswordVisibilityChange = { passwordVisible.value = !passwordVisible.value },

                confirmPassword = confirmPassword.value,
                onConfirmPasswordChange = { confirmPassword.value = it.take(AuthValidator.MAX_PASSWORD_LENGTH) },

                confirmPasswordVisible = confirmPasswordVisible.value,
                onConfirmPasswordVisibilityChange = { confirmPasswordVisible.value = !confirmPasswordVisible.value },

                isRegister = isRegister,
                isForgotPassword = isForgotPassword,

                buttonText = screenConfig.buttonText,
                buttonEnabled = isButtonEnabled,

                onButtonClick = {
                    dismissKeyboard()

                    when (currentScreen) {
                        AuthScreenMode.LOGIN ->
                            viewModel.login(email.value, password.value)

                        AuthScreenMode.REGISTER ->
                            viewModel.register(
                                email.value,
                                password.value,
                                confirmPassword.value
                            )

                        AuthScreenMode.FORGOT_PASSWORD ->
                            viewModel.forgotPassword(email.value)
                    }
                },

                navigateToLogin = { navigateTo(AuthScreenMode.LOGIN) }
            )

            //footer
            AuthScreenFooter(
                footerText = screenConfig.footerText,
                actionText = screenConfig.actionText,
                onActionClick = {
                    navigateTo(
                        when (currentScreen) {
                            AuthScreenMode.LOGIN -> AuthScreenMode.REGISTER
                            AuthScreenMode.REGISTER,
                            AuthScreenMode.FORGOT_PASSWORD -> AuthScreenMode.LOGIN
                        }
                    )
                    clearCredentials()
                }
            )

            //forgot password
            ForgotPassword(
                isLogin = isLogin,
                onForgotPasswordClick = {
                    navigateTo(AuthScreenMode.FORGOT_PASSWORD)
                    clearPasswords()
                }
            )

            //google sign-in

        }
    }
}
