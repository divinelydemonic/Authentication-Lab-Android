package kr.android.basicfirebaseauthentication.auth.presentation.event

sealed interface UiEvent {
    data class ShowSnackBar(val message : String) : UiEvent
}