package kr.android.authenticationlab.auth.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kr.android.authenticationlab.auth.ui.util.AuthOutlinedTextField
import kr.android.authenticationlab.auth.validation.AuthValidator

@Composable
fun AuthScreenCard(
    name: String,
    onNameChange: (String) -> Unit,

    email: String,
    onEmailChange: (String) -> Unit,

    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,

    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    confirmPasswordVisible: Boolean,
    onConfirmPasswordVisibilityChange: () -> Unit,

    isRegister: Boolean,
    isForgotPassword: Boolean,

    buttonText: String,
    buttonEnabled: Boolean,
    onButtonClick: () -> Unit,

    navigateToLogin: () -> Unit
){

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

            //name field
            if (isRegister){
                AuthOutlinedTextField(
                    value = name,
                    label = "Enter name",
                    onValueChange = onNameChange,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    leadingIcon = Icons.Default.Person,
                    leadingIconContentDescription = "name",
                    supportingText = {
                        Text(
                            text = "${name.length}/${AuthValidator.MAX_NAME_LENGTH}",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color =
                                if (name.length >= AuthValidator.MIN_NAME_LENGTH) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        )
                    }
                )
            }

            //email field
            AuthOutlinedTextField(
                value = email,
                label = "Enter email",
                onValueChange = onEmailChange,
                keyboardType = KeyboardType.Email,
                imeAction = if (isForgotPassword) ImeAction.Done else ImeAction.Next,
                leadingIcon = Icons.Default.Email,
                leadingIconContentDescription = "email"
            )

            Spacer(Modifier.height(4.dp))

            //password field
            if (!isForgotPassword){
                AuthOutlinedTextField(
                    value = password,
                    label = "Enter password",
                    onValueChange = onPasswordChange,
                    keyboardType = KeyboardType.Password,
                    imeAction = if (isRegister) ImeAction.Next else ImeAction.Done,
                    leadingIcon = Icons.Default.Password,
                    leadingIconContentDescription = "password",
                    trailingIcon =
                        if (passwordVisible) Icons.Default.VisibilityOff
                        else Icons.Default.Visibility,
                    trailingIconContentDescription =
                        if (passwordVisible) "hide password"
                        else "show password",
                    visualTransformation =
                        if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                    onTrailingIconClick = onPasswordVisibilityChange,
                    supportingText = {
                        Text(
                            text = "${password.length}/${AuthValidator.MAX_PASSWORD_LENGTH}",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color =
                                if (password.length >= AuthValidator.MIN_PASSWORD_LENGTH) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        )
                    }
                )
            }

            //confirm password field
            if (isRegister){
                AuthOutlinedTextField(
                    value = confirmPassword,
                    label = "Confirm password",
                    onValueChange = onConfirmPasswordChange,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    leadingIcon = Icons.Default.Password,
                    leadingIconContentDescription = "confirm password",
                    trailingIcon =
                        if (confirmPasswordVisible) Icons.Default.VisibilityOff
                        else Icons.Default.Visibility,
                    trailingIconContentDescription =
                        if (confirmPasswordVisible) "hide password"
                        else "show password",
                    visualTransformation =
                        if (confirmPasswordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                    onTrailingIconClick = onConfirmPasswordVisibilityChange
                )
            }

            Spacer(Modifier.height(if (isForgotPassword) 0.dp else 8.dp))

            Button(
                onClick = {
                    onButtonClick()
                    if (isForgotPassword){ navigateToLogin() }
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
                enabled = buttonEnabled
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

}