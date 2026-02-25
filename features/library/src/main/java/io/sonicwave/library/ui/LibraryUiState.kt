package io.sonicwave.library.ui

import io.sonicwave.library.domain.model.SortOrder

data class LibraryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val sortOrder: SortOrder = SortOrder.TITLE,
    val isDesc: Boolean = false,
    val isAlbumGroup: Boolean = false
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