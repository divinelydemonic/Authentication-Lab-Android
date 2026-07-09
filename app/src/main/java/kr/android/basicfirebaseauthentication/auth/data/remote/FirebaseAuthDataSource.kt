package kr.android.basicfirebaseauthentication.auth.data.remote

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

/**
 * Handles direct communication with Firebase Authentication.
 * This class is responsible for interacting with Firebase SDK APIs.
 * It should not contain UI logic or application-specific models.
 */
class FirebaseAuthDataSource {

    //firebase auth instance for dependency
    private val firebaseAuth = FirebaseAuth.getInstance()

    /**
     * Returns the currently authenticated Firebase user.
     * Returns null if no user session exists on the device.
     */
    fun getCurrentUser() : FirebaseUser? { return firebaseAuth.currentUser }

    /**
     * Attempts to create a new Firebase Authentication account
     * using the provided email and password.
     * Returns the Firebase authentication result if successful.
     * Throws a Firebase exception if registration fails.
     */
    suspend fun register(
        email : String,
        password: String
    ) : AuthResult {
        return firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .await()
    }

    /**
     * Attempts to sign in using Firebase Authentication
     * and returns the Firebase authentication result.
     * Throws a Firebase exception if authentication fails.
     */
    suspend fun login(
        email : String,
        password : String
    ) : AuthResult {
        return firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .await()
                    // Suspends the coroutine until the Firebase login task completes,
                    // then returns the resulting AuthResult.
    }


    /**
     * Signs out the currently authenticated Firebase user.
     * Clears the local authentication session.
     */
    fun logout(){ firebaseAuth.signOut() }

}