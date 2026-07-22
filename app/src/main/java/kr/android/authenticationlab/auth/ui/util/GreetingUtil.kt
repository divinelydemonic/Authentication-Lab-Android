package kr.android.authenticationlab.auth.ui.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalTime

/**
 * Returns a personalized greeting
 * using the supplied user's name.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun getGreeting(
    name: String
): String {

    val hour = LocalTime.now().hour

    return when (hour) {

        in 5..11 -> "Good Morning, $name 🌅"
        in 12..16 -> "Good Afternoon, $name 🌇"
        in 17..20 -> "Good Evening, $name 🌆"
        in 21 downTo 4 -> "Good Night, $name 🌃"
        else -> "Hello, $name 👋"

    }

}