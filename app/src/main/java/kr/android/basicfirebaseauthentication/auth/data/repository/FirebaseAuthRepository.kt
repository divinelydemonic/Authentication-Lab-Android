package kr.android.basicfirebaseauthentication.auth.data.repository

import kr.android.basicfirebaseauthentication.auth.data.model.UserData
import kr.android.basicfirebaseauthentication.auth.data.remote.FirebaseAuthDataSource

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
     * Creates a new Firebase Authentication account and
     * maps the authenticated Firebase user to the application's
     * UserData model.
     * Returns a Result containing the authenticated user
     * or the registration failure.
     */
    override suspend fun register(
        email: String,
        password: String
    ): Result<UserData> {

        try {
            val authResult = dataSource.register(email, password)
            val firebaseUser = authResult.user
                ?: return Result.failure(IllegalStateException("User is missing!"))
            val userEmail = firebaseUser.email
                ?: return Result.failure(IllegalStateException("Email is missing!"))

            return Result.success(
                value = UserData(
                    uid = firebaseUser.uid,
                    email = userEmail
                )
            )
        }
        catch (exception : Exception) {
            return Result.failure(exception)
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
     * Signs out the currently authenticated user.
     */
    override fun logout(){ dataSource.logout() }

}
