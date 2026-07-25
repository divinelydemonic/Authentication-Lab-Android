package kr.android.authenticationlab.auth.data.repository

import kr.android.authenticationlab.auth.data.model.UserData

/**
 * Contract defining authentication-related operations.
 *
 * The presentation layer depends on this interface rather than a
 * concrete implementation, allowing the authentication provider
 * to be replaced without affecting the rest of the application.
 */
interface AuthRepository {

    /**
     * Returns the currently authenticated user.
     *
     * @return The authenticated UserData, or null if no user is signed in.
     */
    fun getCurrentUser() : UserData?

    /**
     * Authenticates the user using a Google ID token.
     *
     * @param idToken Google ID token obtained from Credential Manager.
     * @return A Result containing the authenticated UserData.
     */
    suspend fun signInWithGoogle(idToken: String): Result<UserData>

    /**
     * Registers a new user account.
     *
     * @param name User's display name.
     * @param email User's email address.
     * @param password User's password.
     * @return A Result indicating whether registration succeeded.
     */
    suspend fun register(
        name: String,
        email: String,
        password: String
    ) : Result<Unit>

    /**
     * Sends another verification email to the authenticated user.
     *
     * @return A Result indicating whether the request succeeded.
     */
    suspend fun resendVerificationEmail(): Result<Unit>

    /**
     * Returns whether the authenticated user's email address
     * has been verified.
     *
     * @return A Result containing the verification status.
     */
    suspend fun isEmailVerified(): Result<Boolean>

    /**
     * Signs in an existing user.
     *
     * @param email User's email address.
     * @param password User's password.
     * @return A Result containing the authenticated UserData.
     */
    suspend fun login(
        email : String,
        password : String
    ) : Result<UserData>

    /**
     * Sends a password reset email.
     *
     * @param email Email address associated with the account.
     * @return A Result indicating whether the request succeeded.
     */
    suspend fun forgotPassword(email: String) : Result<Unit>

    /**
     * Signs out the currently authenticated user.
     */
    fun logout()

}