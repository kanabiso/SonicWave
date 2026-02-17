package io.sonicwave.library.ui

sealed interface LibraryUiEvent {
    data class OnTrackClick(val trackId: String) : LibraryUiEvent
}