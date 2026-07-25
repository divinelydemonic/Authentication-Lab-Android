package kr.android.authenticationlab.auth.data.mapper

import com.google.firebase.auth.FirebaseUser
import kr.android.authenticationlab.auth.data.model.UserData


/**
 * Maps a Firebase Authentication user model to the application's
 * UserData model.
 *
 * This prevents Firebase SDK models from leaking outside
 * the data layer.
 */
fun FirebaseUser.toUserData(): UserData {
    return UserData(
        uid = uid,
        name = displayName.orEmpty(),
        email = email.orEmpty()
    )
}