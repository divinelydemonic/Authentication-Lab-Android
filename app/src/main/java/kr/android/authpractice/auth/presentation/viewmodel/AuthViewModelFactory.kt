package kr.android.authpractice.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kr.android.authpractice.auth.data.repository.AuthRepository

/**
 * Factory responsible for creating instances of AuthViewModel.
 * Supplies the required repository dependency when
 * Android requests a new AuthViewModel instance.
 */
class AuthViewModelFactory(
    private val repository: AuthRepository
) : ViewModelProvider.Factory {

    /**
     * Creates and returns an AuthViewModel when requested.
     * Throws an exception if an unsupported ViewModel
     * class is requested.
     */
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        // Verify that the requested ViewModel class
        // is AuthViewModel.
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {

            // Safe because the requested class has already
            // been verified as AuthViewModel and hence cast as T
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }

        // Reject unsupported ViewModel classes.
        throw IllegalArgumentException(
            "Unknown ViewModel class."
        )
    }
}