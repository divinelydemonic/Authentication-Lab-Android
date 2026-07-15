package kr.android.authenticationlab.auth.validation

sealed interface ValidationResult {
    data object Success : ValidationResult
    data class Failure(val message : String) : ValidationResult
}