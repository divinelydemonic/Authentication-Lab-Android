package kr.android.authenticationlab.auth.data.repository

import kr.android.authenticationlab.auth.data.model.UserData
import kr.android.authenticationlab.auth.data.remote.FirebaseAuthDataSource

/**
 * Converts Firebase-specific user models into
 * application-specific user models.
 * This prevents Firebase classes from leaking
 * into the presentation layer.
 */
class FirebaseAuthRepository (
    private val dataSource: FirebaseAuthDataSource
) : AuthRepository {

    //function to get current user details
    override fun getCurrentUser(): UserData? {

        //fresh data retrieval from data source when needed
        val firebaseUser = dataSource.getCurrentUser()
        val userEmail = firebaseUser?.email

        return when {

            // No authenticated Firebase user exists
            (firebaseUser == null) -> null

            // No email available for this user
            (userEmail == null) -> null

            //convert firebase user model to app user model
            else -> {
                UserData(
                    uid = firebaseUser.uid,
                    email = userEmail
                )
            }

        }
    }


    /**
     * Creates a new Firebase Authentication account,
     * sends a verification email to the newly registered user,
     * and returns a successful Result when both operations complete.
     */
    override suspend fun register(
        email: String,
        password: String
    ): Result<Unit> {

        return try {
            val authResult = dataSource.register(email, password)
            val firebaseUser = authResult.user
                ?: return Result.failure(IllegalStateException("User is missing!"))

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
     * Authenticates the user through the Firebase data source,
     * converts Firebase models into application models, and
     * returns the authentication result.
     */
    override suspend fun login(
        email : String,
        password : String
    ) : Result<UserData> {

        try {

            // Authenticate the user through Firebase.
            val authResult = dataSource.login(email, password)

            // Retrieve the authenticated Firebase user and exception if null
            val firebaseUser = authResult.user
                ?: return Result.failure(IllegalStateException("User is missing!"))

            // Retrieve the authenticated user's email and exception if null
            val userEmail = firebaseUser.email
                ?: return Result.failure(IllegalStateException("Email is missing!"))

            // Convert the Firebase model into the application's
            // UserData model and return a successful result
            return Result.success(
                UserData(
                    uid = firebaseUser.uid,
                    email = userEmail
                )
            )

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
