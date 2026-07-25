package kr.android.authenticationlab.auth.data.remote

import com.google.firebase.auth.*
import kotlinx.coroutines.tasks.await

/**
 * Data source responsible for direct communication with Firebase Authentication.
 *
 * This class encapsulates all Firebase Authentication SDK calls and exposes
 * Firebase-specific models to the repository layer.
 *
 * It should not contain business logic, UI logic, or application-specific models.
 */
class FirebaseAuthDataSource {

    // Firebase Authentication instance used for all authentication operations.
    private val firebaseAuth = FirebaseAuth.getInstance()

    /**
     * Returns the currently authenticated Firebase user.
     *
     * @return The authenticated [FirebaseUser], or null if no user is signed in.
     */
    fun getCurrentUser() : FirebaseUser? { return firebaseAuth.currentUser }

    /**
      * Authenticates the user with Firebase using a Google ID token.

      * This function:
      * 1. Creates a Firebase AuthCredential from the Google ID token.
      * 2. Sends the credential to Firebase Authentication.
      * 3. Suspends until authentication completes.
      * 4. Returns the authenticated Firebase user.

      * @param idToken Google ID token received from Credential Manager.
      * @return The authenticated Firebase user, or null if no user is available.
      * @throws Exception If Firebase Authentication fails.
     */
    suspend fun signInWithGoogle(idToken: String) : FirebaseUser? {

        // Create a Firebase credential from the Google ID token
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        // Authenticate with Firebase and suspend until the request completes
        val authResult =
            firebaseAuth
                .signInWithCredential(credential)
                .await()

        // Return the authenticated Firebase user
        return authResult.user
    }

    /**
     * Updates the profile information of the authenticated Firebase user.
     *
     * Currently, updates only the user's display name.
     *
     * @param user Authenticated Firebase user.
     * @param name Display name to be stored in Firebase Authentication.
     * @throws Exception If the profile update fails.
     */
    suspend fun updateUserProfile(
        user: FirebaseUser,
        name: String
    ){

        val profileUpdates =
            userProfileChangeRequest { displayName = name }

        user.updateProfile(profileUpdates).await()
    }

    /**
     * Creates a new Firebase Authentication account using the provided
     * email and password.
     *
     * @param email User's email address.
     * @param password User's password.
     * @return The newly authenticated Firebase user.
     * @throws Exception If account creation fails.
     */
    suspend fun register(
        email : String,
        password: String
    ) : FirebaseUser? {

        val authResult =
            firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

        return authResult.user
    }

    /**
     * Sends an email verification link to the currently
     * authenticated Firebase user.
     * This function waits until Firebase completes the request.
     * Any Firebase exceptions are propagated to the repository layer.
     *
     * @param user Authenticated Firebase user.
     */
    suspend fun sendVerificationEmail(user : FirebaseUser){
        user.sendEmailVerification()
            .await()
    }

    /**
     * Reloads the authenticated user's information from Firebase.
     *
     * This fetches the latest account state from the server, such as
     * email verification status or profile updates.
     *
     * @param user Authenticated Firebase user.
     * @throws Exception If the reload operation fails.
     */
    suspend fun reloadUser(user : FirebaseUser){
        user.reload()
            .await()
    }

    /**
     * Signs in an existing user using email and password.
     *
     * @param email User's email address.
     * @param password User's password.
     * @return The authenticated Firebase user.
     * @throws Exception If authentication fails.
     */
    suspend fun login(
        email : String,
        password : String
    ) : FirebaseUser? {

        val authResult =
            firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()    // Suspend until Firebase completes authentication.

        return authResult.user
    }

    /**
     * Sends a password reset email to the specified email address.
     *
     * @param email Email address associated with the Firebase account.
     * @throws Exception If the request fails.
     */
    suspend fun forgotPassword(email : String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .await()
    }

    /**
     * Signs out the currently authenticated user.
     *
     * This clears the local Firebase Authentication session on the device.
     */
    fun logout(){ firebaseAuth.signOut() }

}