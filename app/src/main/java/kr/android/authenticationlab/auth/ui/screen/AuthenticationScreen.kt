package kr.android.authenticationlab.auth.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kr.android.authenticationlab.auth.presentation.viewmodel.AuthViewModel
import kr.android.authenticationlab.auth.validation.AuthValidator

@Composable
fun AuthenticationScreen(
    viewModel: AuthViewModel
){

    //checks login mode or registration mode
    val isLoginMode = remember { mutableStateOf(true) }

    //dynamically changing values according to mode
    val titleText =
        if (isLoginMode.value) "Login" else "Sign Up"

    val buttonText =
        if (isLoginMode.value) "Login" else "Sign Up"

    val footerText =
        if (isLoginMode.value) "Don't have an account?"
        else "Already have an account?"

    val actionText =
        if (isLoginMode.value) "Sign Up" else "Login"

    val email = remember { mutableStateOf("") }

    val password = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }

    val confirmPassword = remember { mutableStateOf("") }
    val confirmPasswordVisible = remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val dismissKeyboard = {
        focusManager.clearFocus()
        softwareKeyboardController?.hide()
    }


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

            Text(
                text = titleText,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.displayMedium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .widthIn(max = 400.dp)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .padding(vertical = 32.dp, horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    //email field
                    CredentialField(
                        value = email.value,
                        label = "Enter email",
                        onValueChange = { email.value = it },
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                        leadingIcon = Icons.Default.Email,
                        leadingIconContentDescription = "email"
                    )

                    Spacer(Modifier.height(4.dp))

                    //password field
                    CredentialField(
                        value = password.value,
                        label = "Enter password",
                        onValueChange = { password.value = it.take(AuthValidator.MAX_PASSWORD_LENGTH) },
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next,
                        leadingIcon = Icons.Default.Password,
                        leadingIconContentDescription = "password",
                        trailingIcon =
                            if (passwordVisible.value) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                        trailingIconContentDescription =
                            if (passwordVisible.value) "hide password"
                            else "show password",
                        visualTransformation =
                            if (passwordVisible.value) VisualTransformation.None
                            else PasswordVisualTransformation(),
                        onTrailingIconClick = { passwordVisible.value = !passwordVisible.value },
                        supportingText = {
                            Text(
                                text = "${password.value.length}/${AuthValidator.MAX_PASSWORD_LENGTH}",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color =
                                    if (password.value.length >= AuthValidator.MIN_PASSWORD_LENGTH) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.End,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            )
                        }
                    )

                    //confirm password field
                    if (!isLoginMode.value){
                        CredentialField(
                            value = confirmPassword.value,
                            label = "Confirm password",
                            onValueChange = { confirmPassword.value = it.take(AuthValidator.MAX_PASSWORD_LENGTH) },
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                            leadingIcon = Icons.Default.Password,
                            leadingIconContentDescription = "confirm password",
                            trailingIcon =
                                if (confirmPasswordVisible.value) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                            trailingIconContentDescription =
                                if (confirmPasswordVisible.value) "hide password"
                                else "show password",
                            visualTransformation =
                                if (confirmPasswordVisible.value) VisualTransformation.None
                                else PasswordVisualTransformation(),
                            onTrailingIconClick = { confirmPasswordVisible.value = !confirmPasswordVisible.value }
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = {
                            dismissKeyboard()

                            if (isLoginMode.value) viewModel.login(email.value, password.value)
                            else viewModel.register(email.value, password.value, confirmPassword.value)

                        },
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            disabledElevation = 0.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                        ),
                        enabled =
                            email.value.isNotBlank() &&
                                    password.value.isNotBlank() &&
                                    password.value.length >= AuthValidator.MIN_PASSWORD_LENGTH &&
                                    (isLoginMode.value || confirmPassword.value.isNotBlank())
                    ) {
                        Text(
                            text = buttonText,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                }

            }

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = footerText,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )

                TextButton(
                    onClick = {

                        isLoginMode.value = !isLoginMode.value

                        password.value = ""
                        confirmPassword.value  = ""
                    }
                ) {
                    Text(
                        text = actionText,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = TextDecoration.Underline,
                        color = MaterialTheme.colorScheme.inverseSurface,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

        }
    }
}

@Composable
fun CredentialField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    leadingIcon: ImageVector,
    leadingIconContentDescription: String?,
    trailingIcon: ImageVector? = null,
    trailingIconContentDescription: String? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    supportingText: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
){

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold
        ),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = leadingIconContentDescription
            )
        },
        trailingIcon = {
            trailingIcon?.let{
                if (onTrailingIconClick != null){
                    IconButton(
                        onClick = onTrailingIconClick
                    ){
                        Icon(
                            imageVector = it,
                            contentDescription = trailingIconContentDescription
                        )
                    }
                } else {
                    Icon(
                        imageVector = it,
                        contentDescription = trailingIconContentDescription
                    )
                }
            }
        },
        visualTransformation = visualTransformation,
        supportingText = supportingText,
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
            focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
            errorTextColor = MaterialTheme.colorScheme.error,
            errorCursorColor = MaterialTheme.colorScheme.error,
            errorLabelColor = MaterialTheme.colorScheme.error,
            errorBorderColor = MaterialTheme.colorScheme.error,
            errorLeadingIconColor = MaterialTheme.colorScheme.error,
            errorTrailingIconColor = MaterialTheme.colorScheme.error,
        )
    )
}
