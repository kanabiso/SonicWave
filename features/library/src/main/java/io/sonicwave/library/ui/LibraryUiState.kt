package io.sonicwave.library.ui

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class LibraryUiState(
    val audioFiles: ImmutableList<TrackUiModel> = persistentListOf(),
    val isLoading: Boolean = false
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