package io.sonicwave.library.ui

data class LibraryUiState(
    val audioFiles: List<TrackUiModel> = emptyList(),
    val isLoading: Boolean = false,
)

data class TrackUiModel(
    val id: String,
    val title: String,
    val artist: String,
    val album: String? = null,
    val duration: String,

    val isPlaying: Boolean = false,
    val isSelected: Boolean = false
)