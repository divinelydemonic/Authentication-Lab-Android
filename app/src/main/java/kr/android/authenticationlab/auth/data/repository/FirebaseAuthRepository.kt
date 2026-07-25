package kr.android.authenticationlab.auth.data.repository

import kr.android.authenticationlab.auth.data.mapper.toUserData
import kr.android.authenticationlab.auth.data.model.UserData
import kr.android.authenticationlab.auth.data.remote.FirebaseAuthDataSource

/**
 * Repository implementation backed by Firebase Authentication.
 *
 * This repository coordinates authentication-related operations through
 * the FirebaseAuthDataSource and converts Firebase SDK models into
 * application-specific models.
 *
 * The presentation layer interacts only with UserData and Result types,
 * remaining independent of Firebase Authentication APIs.
 */
class FirebaseAuthRepository (
    private val dataSource: FirebaseAuthDataSource
) : AuthRepository {

    /**
     * Returns the currently authenticated user as an application-specific model.
     *
     * @return The authenticated UserData, or null if no user is signed in.
     */
    override fun getCurrentUser(): UserData? {

        val firebaseUser = dataSource.getCurrentUser() ?: return null

        return firebaseUser.toUserData()
    }

    /**
     * Authenticates the user with Firebase using a Google ID token.
     *
     * Converts the authenticated Firebase user into the application's UserData model.
     *
     * @param idToken Google ID token obtained from Credential Manager.
     * @return A Result containing the authenticated UserData on success.
     */
    override suspend fun signInWithGoogle(idToken: String): Result<UserData> {

        return try {

            val firebaseUser = dataSource.signInWithGoogle(idToken)
                ?: return Result.failure(IllegalStateException("User is missing!"))

            Result.success(firebaseUser.toUserData())

        }
        catch (exception: Exception){ Result.failure(exception) }

    }


    /**
     * Registers a new user with Firebase Authentication.
     *
     * After successful account creation, this function:
     * 1. Updates the user's display name.
     * 2. Sends an email verification link.
     *
     * @param name User's display name.
     * @param email User's email address.
     * @param password User's password.
     * @return A successful Result when registration completes.
     * @throws Exception Propagated as a failed Result if any operation fails.
     */
    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<Unit> {

        return try {

            val firebaseUser = dataSource.register(email, password)
                ?: return Result.failure(IllegalStateException("User is missing!"))

            dataSource.updateUserProfile(user = firebaseUser, name = name)

            dataSource.sendVerificationEmail(firebaseUser)

            Result.success(Unit)
        }
        catch (exception : Exception) {
            Result.failure(exception)
        }

    }

    /**
     * Sends another email verification link to the currently
     * authenticated user.
     * Returns a successful Result if the verification email
     * is sent, or a failed Result if the request fails.
     */
    override suspend fun resendVerificationEmail(): Result<Unit> {

        return try {

            val firebaseUser = dataSource.getCurrentUser()
                ?: return Result.failure(IllegalStateException("User is missing!"))

            dataSource.sendVerificationEmail(firebaseUser)

            Result.success(Unit)
        }
        catch (exception : Exception){
            Result.failure(exception)
        }

    }

    /**
     * Refreshes the currently authenticated Firebase user
     * and returns whether the user's email has been verified.
     * Returns a failed Result if no authenticated user exists
     * or if the refresh request fails.
     */
    override suspend fun isEmailVerified(): Result<Boolean> {

        return try {

            val firebaseUser = dataSource.getCurrentUser()
                ?: return Result.failure(IllegalStateException("User is missing!"))

            dataSource.reloadUser(firebaseUser)

            Result.success(firebaseUser.isEmailVerified)

        }
        catch (exception : Exception){
            Result.failure(exception)
        }

    }


    /**
     * Signs in an existing user using email and password.
     *
     * Converts the authenticated Firebase user into the application's
     * UserData model.
     *
     * @param email User's email address.
     * @param password User's password.
     * @return A Result containing UserData on success.
     */
    override suspend fun login(
        email : String,
        password : String
    ) : Result<UserData> {

        try {

            val firebaseUser = dataSource.login(email, password)
                ?: return Result.failure(IllegalStateException("User is missing!"))

            // Convert the Firebase model into the application's
            // UserData model and return a successful result
            return Result.success(firebaseUser.toUserData())

        }
        catch (exception : Exception) {
            // Preserve the original authentication exception
            // and return it as a failed result
            return Result.failure(exception)
        }

    }

    /**
     * Sends a password reset email to the provided email address.
     * Returns a successful Result if the password reset email is sent,
     * or a failed Result if the request fails.
     */
    override suspend fun forgotPassword(email: String): Result<Unit> {
        return try {

            dataSource.forgotPassword(email)

            Result.success(Unit)

        } catch (exception : Exception){ Result.failure(exception) }
    }


    /**
     * Signs out the currently authenticated user.
     */
    override fun logout(){ dataSource.logout() }

}
