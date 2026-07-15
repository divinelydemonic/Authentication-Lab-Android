package kr.android.authenticationlab.auth.validation

import android.util.Patterns

object AuthValidator {

    const val MIN_PASSWORD_LENGTH = 8
    const val MAX_PASSWORD_LENGTH = 16

    /**
     * Validates the email against
     * the Google's email policy.
     */
    fun validateEmail (email : String) : ValidationResult {

        if (email.isBlank())
            return ValidationResult.Failure("Email cannot be empty.")

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return ValidationResult.Failure("Please enter a valid email address.")

        return ValidationResult.Success

    }

    /**
     * Validates the password against
     * the application's password policy.
     */
    fun validatePassword (password : String) : ValidationResult {

        if (password.isBlank())
            return ValidationResult.Failure("Password cannot be empty.")

        if (password.length !in MIN_PASSWORD_LENGTH..MAX_PASSWORD_LENGTH)
            return ValidationResult.Failure("Password must be between $MIN_PASSWORD_LENGTH and $MAX_PASSWORD_LENGTH characters long.")

        if (!password.any{it.isDigit()})
            return ValidationResult.Failure("Password must have at least one digit.")

        if (!password.any{it.isUpperCase()})
            return ValidationResult.Failure("Password must contain at least one uppercase letter.")

        if (!password.any{it.isLowerCase()})
            return ValidationResult.Failure("Password must contain at least one lowercase letter.")

        if (!password.any {!it.isLetterOrDigit()})
            return ValidationResult.Failure("Password must contain at least one special character.")

        return ValidationResult.Success
    }

    //validate password confirmation field
    fun validateConfirmPassword (
        password : String,
        confirmPassword : String
    ) : ValidationResult {

        if (confirmPassword.isBlank())
            return ValidationResult.Failure("Please confirm your password.")

        if (password != confirmPassword)
            return ValidationResult.Failure("Passwords do not match.")

        return ValidationResult.Success

    }

}
