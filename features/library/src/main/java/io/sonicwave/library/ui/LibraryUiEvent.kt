package io.sonicwave.library.ui

import io.sonicwave.library.domain.model.SortOrder

sealed interface LibraryUiEvent {
    data class OnTrackClick(val trackId: String) : LibraryUiEvent
    data class OnTrackLongClick(val trackId: String) : LibraryUiEvent
    data object OnFilterClick : LibraryUiEvent
    data class OnApplyFilters(
        val sortOrder: SortOrder,
        val isDesc: Boolean,
        val isAlbumGroup: Boolean,
        val filterQuery: String
    ) : LibraryUiEvent
}