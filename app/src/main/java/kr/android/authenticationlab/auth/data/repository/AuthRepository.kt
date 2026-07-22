package kr.android.authenticationlab.auth.data.repository

import kr.android.authenticationlab.auth.data.model.UserData

/**
 * Contract for authentication-related operations.
 * The ViewModel depends on this interface instead of a concrete
 * Firebase implementation, allowing the authentication provider
 * to be replaced without affecting the presentation layer.
 */
interface AuthRepository {

    /**
     * Returns the currently authenticated user if a session exists.
     * Returns null when no user is authenticated.
     */
    fun getCurrentUser() : UserData?

    /**
     * Attempts to create a new user account using the provided
     * name, email and password.
     * Sends a verification email to the newly registered user.
     * Returns a successful Result when both operations complete,
     * or a failure if either operation fails.
     */
    suspend fun register(
        name: String,
        email: String,
        password: String
    ) : Result<Unit>

    /**
     * Sends another email verification link to the currently
     * authenticated user.
     * Returns a successful Result if the verification email
     * is sent, or a failed Result if the request fails.
     */
    suspend fun resendVerificationEmail(): Result<Unit>

    /**
     * Refreshes the currently authenticated user from Firebase
     * and returns whether their email has been verified.
     * Returns a failed Result if no authenticated user exists
     * or if the refresh request fails.
     */
    suspend fun isEmailVerified(): Result<Boolean>

    /**
     * Attempts to authenticate the user using the provided email and password.
     * Returns a successful Result containing the authenticated UserData,
     * or a failed Result if authentication fails.
     */
    suspend fun login(
        email : String,
        password : String
    ) : Result<UserData>

    /**
     * Sends a password reset email to the provided email address.
     * Returns a successful Result if the password reset email is sent,
     * or a failed Result if the request fails.
     */
    suspend fun forgotPassword(email: String) : Result<Unit>

    /**
     * Signs out the currently authenticated user.
     */
    fun logout()

}