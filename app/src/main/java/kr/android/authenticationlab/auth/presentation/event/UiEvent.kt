package kr.android.authenticationlab.auth.presentation.event

sealed interface UiEvent {
    data class ShowSnackBar(val message : String) : UiEvent
}