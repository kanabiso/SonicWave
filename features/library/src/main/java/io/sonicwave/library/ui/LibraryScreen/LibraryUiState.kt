package io.sonicwave.library.ui.LibraryScreen

import io.sonicwave.library.domain.model.SortOrder

data class LibraryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val sortOrder: SortOrder = SortOrder.TITLE,
    val isDesc: Boolean = false,
    val isAlbumGroup: Boolean = false,
    val isAlbumListLayout: Boolean = false,
    val isTracksListLayout: Boolean = true,
    val filterQuery: String = ""
)

sealed interface LibraryItemUiModel {
    val id: Long
}

data class TrackUiModel(
    override val id: Long,
    val title: String?,
    val artist: String?,
    val year: String? = null,
    val album: String?,
    val albumId: Long,
    val track: Int,
    val duration: String,
    val coverUri: String,
    val isPlaying: Boolean = false,
    val isSelected: Boolean = false
) : LibraryItemUiModel

data class AlbumUiModel(
    override val id: Long,
    val name: String?,
    val artist: String?,
    val year: String? = null,
    val trackCount: Int?,
    val duration: String,
    val coverUri: String,
) : LibraryItemUiModel