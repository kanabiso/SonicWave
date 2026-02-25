package io.sonicwave.library.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.sonicwave.common.utils.formatAsDuration
import io.sonicwave.library.domain.usecase.GetAudioTracksUseCase
import io.sonicwave.library.domain.usecase.PlayTrackUseCase
import io.sonicwave.media.model.AudioTrack
import jakarta.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getAudioTracksUseCase: GetAudioTracksUseCase,
    private val playTrackUseCase: PlayTrackUseCase,
) : ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow(LibraryUiState(isLoading = true))
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    // Audio tracks from domain
    val audioTracks: StateFlow<ImmutableList<TrackUiModel>> = combine(
        getAudioTracksUseCase(),
        _uiState
    ) { tracks: List<AudioTrack>, state: LibraryUiState ->

        // Sort logic
        val sortedTracks = when (state.sortOrder) {
            SortOrder.TITLE -> if (state.isDesc) tracks.sortedByDescending { it.title } else tracks.sortedBy { it.title }
            SortOrder.AUTHOR -> if (state.isDesc) tracks.sortedByDescending { it.artist } else tracks.sortedBy { it.artist }
            SortOrder.DURATION -> if (state.isDesc) tracks.sortedByDescending { it.durationMs } else tracks.sortedBy { it.durationMs }
            SortOrder.LAST_ADDED -> if (state.isDesc) tracks.sortedByDescending { it.dateAdded } else tracks.sortedBy { it.dateAdded }
        }

        // mapper
        sortedTracks
            .map { it.toUiModel(isPlaying = false) }
            .toImmutableList()
    }
        .onStart {
            _uiState.update { it.copy(isLoading = false) }
        }
        .catch { exception ->
            _uiState.update { it.copy(isLoading = false, errorMessage = exception.message) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = persistentListOf()
        )

    fun onEvent(event: LibraryUiEvent) {
        when (event) {
            is LibraryUiEvent.OnTrackClick -> playTrackUseCase(event.trackId)
            is LibraryUiEvent.OnTrackLongClick -> {}
            is LibraryUiEvent.OnFilterClick -> {

            }
            is LibraryUiEvent.OnApplyFilters -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        sortOrder = event.sortOrder,
                        isDesc = event.isDesc,
                        isAlbumGroup = event.isAlbumGroup,
                    )
                }
            }
        }
    }

    private fun AudioTrack.toUiModel(isPlaying: Boolean): TrackUiModel {
        return TrackUiModel(
            id = this.id,
            title = this.title,
            artist = this.artist,
            album = this.album,
            duration = this.durationMs.formatAsDuration(),
            isPlaying = isPlaying,
        )
    }
}