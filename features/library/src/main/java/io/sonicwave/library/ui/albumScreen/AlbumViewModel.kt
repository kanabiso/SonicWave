package io.sonicwave.library.ui.albumScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.sonicwave.library.domain.usecase.GetTracksFromAlbumUseCase
import io.sonicwave.library.domain.usecase.PlayTrackUseCase
import io.sonicwave.library.ui.mappers.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val getTracksFromAlbumUseCase: GetTracksFromAlbumUseCase,
    private val playTrackUseCase: PlayTrackUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(AlbumUiState(albumId = null))
    val uiState: StateFlow<AlbumUiState> = _uiState.asStateFlow()

    init {
        Log.i("ViewModel", "init viewmodel")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("ViewModel", "cleared viewmodel")
    }

    fun onTrackClick(trackId: Long) {
        viewModelScope.launch {
            playTrackUseCase(trackId)
        }
    }

    fun loadAlbum(id: Long) {
        if (_uiState.value.albumId == id && _uiState.value.tracks.isNotEmpty()) return

        _uiState.value = _uiState.value.copy(albumId = id)

        val songs = getTracksFromAlbumUseCase(id).map { track ->
            track.toUiModel(isPlaying = false)
        }

        if (songs[0].track == songs[1].track) {
            songs.sortedBy { it.title }
        } else {
            songs.sortedBy { it.track }
        }

        _uiState.value = _uiState.value.copy(
            name = songs.firstOrNull()?.album ?: "",
            artist = songs.firstOrNull()?.artist ?: "",
            year = songs.firstOrNull()?.year ?: "",
            trackCount = songs.size,
            duration = songs.firstOrNull()?.duration ?: "",
            coverUri = songs.firstOrNull()?.coverUri ?: "",
            tracks = songs
        )
    }
}