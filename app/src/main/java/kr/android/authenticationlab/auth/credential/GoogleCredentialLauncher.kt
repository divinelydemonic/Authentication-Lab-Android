package kr.android.authenticationlab.auth.credential

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kr.android.authenticationlab.BuildConfig

/**
 * Launches the Google Sign-In flow using Android's Credential Manager
 * and returns a Google ID token for Firebase authentication.
 */
class GoogleCredentialLauncher(
    private val context: Context
) {

    // Creates the Credential Manager used to request user credentials.
    private val credentialManager = CredentialManager.create(context)

    // Credential request used to initiate the Google Sign-In flow.
    private val request =
        GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(false)
                    .setAutoSelectEnabled(true)
                    .build()
            )
            .build()

    /**
     * Launches the Google Sign-In flow and retrieves a Google ID token.
     *
     * @return A Result containing the Google ID token on success, or the
     * exception that caused the sign-in request to fail.
     */
    suspend fun getGoogleIdToken(): Result<String>{

        // Request a Google credential from Credential Manager
        return try {

            // Requests a credential from Credential Manager.
            // Suspends until the user selects an account or cancels.
            val result = credentialManager.getCredential(context, request)

            // Extract the credential from the response
            val credential = result.credential

            // Verify that the returned credential is a Google ID token.
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {

                // Convert the credential into a GoogleIdTokenCredential.
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)

                // Extract the Google ID token.
                val idToken = googleIdTokenCredential.idToken

                Result.success(idToken)

            } else {
                Result.failure(IllegalStateException("Unsupported credential type."))
            }
        }

        // The user dismissed the Google account picker.
        catch (exception: GetCredentialCancellationException) { Result.failure(exception) }

        // No Google credentials are available on the device.
        catch (exception: NoCredentialException) { Result.failure(exception) }

        // Credential Manager failed to complete the request.
        catch (exception: GetCredentialException) { Result.failure(exception) }
    }

}