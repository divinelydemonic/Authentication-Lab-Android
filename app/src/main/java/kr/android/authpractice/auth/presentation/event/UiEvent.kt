package kr.android.authpractice.auth.presentation.event

sealed interface UiEvent {
    data class ShowSnackBar(val message : String) : UiEvent
}