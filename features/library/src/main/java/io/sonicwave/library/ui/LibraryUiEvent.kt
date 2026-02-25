package io.sonicwave.library.ui

sealed interface LibraryUiEvent {
    data class OnTrackClick(val trackId: String) : LibraryUiEvent
    data class OnTrackLongClick(val trackId: String) : LibraryUiEvent
    object OnFilterClick : LibraryUiEvent
    data class OnApplyFilters(
        val sortOrder: SortOrder,
        val isDesc: Boolean,
        val isAlbumGroup: Boolean,
        val filterQuery: String
    ) : LibraryUiEvent
}