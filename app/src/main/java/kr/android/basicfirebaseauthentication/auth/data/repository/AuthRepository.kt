package kr.android.basicfirebaseauthentication.auth.data.repository

import kr.android.basicfirebaseauthentication.auth.data.model.UserData

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
     * email and password.
     * Returns the authenticated user if registration succeeds,
     * or a failure if registration fails.
     */
    suspend fun register(
        email: String,
        password: String
    ) : Result<UserData>

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
     * Signs out the currently authenticated user.
     */
    fun logout()

}