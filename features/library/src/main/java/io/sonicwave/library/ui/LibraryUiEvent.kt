package io.sonicwave.library.ui

import io.sonicwave.library.domain.model.SortOrder

sealed interface LibraryUiEvent {
    data class OnTrackClick(val trackId: Long) : LibraryUiEvent
    data class OnTrackLongClick(val trackId: Long) : LibraryUiEvent
    data object OnFilterClick : LibraryUiEvent
    data class OnApplyFilters(
        val sortOrder: SortOrder,
        val isDesc: Boolean,
        val isAlbumGroup: Boolean,
        val filterQuery: String
    ) : LibraryUiEvent

    data object OnAlbumIconClick : LibraryUiEvent
    data object OnAlbumLayoutToggleClick : LibraryUiEvent
    data object OnTracksLayoutToggleClick : LibraryUiEvent
    data object OnPermissionsGranted : LibraryUiEvent

}