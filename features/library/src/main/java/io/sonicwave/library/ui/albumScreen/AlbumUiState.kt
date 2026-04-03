package io.sonicwave.library.ui.albumScreen

import io.sonicwave.library.ui.LibraryScreen.TrackUiModel


data class AlbumUiState(
    val albumId: Long? = null,
    val name: String = "",
    val artist: String = "",
    val year: String? = null,
    val trackCount: Int = 0,
    val duration: String = "",
    val coverUri: String = "",
    val tracks: List<TrackUiModel> = emptyList()
)
