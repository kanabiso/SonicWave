package io.sonicwave.library.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.sonicwave.common.utils.formatAsDuration
import io.sonicwave.library.domain.usecase.GetAudioTracksUseCase
import io.sonicwave.library.domain.usecase.PlayTrackUseCase
import io.sonicwave.media.model.AudioTrack
import jakarta.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getAudioTracksUseCase: GetAudioTracksUseCase,
    private val playTrackUseCase: PlayTrackUseCase,
) : ViewModel() {

    val uiState: StateFlow<LibraryUiState> = getAudioTracksUseCase()
        .map { tracks ->
            LibraryUiState(
                audioFiles = tracks.map { track -> track.toUiModel(isPlaying = false) }.toImmutableList(),
                isLoading = false
            )
        }
        .onStart {
            emit(LibraryUiState(isLoading = true))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LibraryUiState(isLoading = true)
        )

    fun onEvent(event: LibraryUiEvent) {
        when (event) {
            is LibraryUiEvent.OnTrackClick -> playTrackUseCase(event.trackId)
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