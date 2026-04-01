package io.sonicwave.player.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import io.sonicwave.media.domain.interfaces.MusicPlayer
import io.sonicwave.media.domain.model.AudioTrack
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val musicPlayer: MusicPlayer
) : ViewModel() {

    private val _isMinimized = MutableStateFlow(true)

    val uiState: StateFlow<PlayerUiState> = combine(
        musicPlayer.currentTrack,
        musicPlayer.isPlaying,
        musicPlayer.currentPosition,
        musicPlayer.shuffleModeEnabled,
        musicPlayer.repeatMode,
        _isMinimized
    ) { params ->
        val track = params[0] as AudioTrack?
        val isPlaying = params[1] as Boolean
        val position = params[2] as Long
        val shuffle = params[3] as Boolean
        val repeat = params[4] as Int
        val isMinimized = params[5] as Boolean

        PlayerUiState(
            track = track?.toUiModel() ?: MockTrack(
                title = "No Track",
                artist = "Unknown",
                album = "Unknown",
                coverArtUrl = null,
                durationMs = 0L
            ),
            isPlaying = isPlaying,
            isLooping = repeat != Player.REPEAT_MODE_OFF,
            isShuffleEnabled = shuffle,
            currentPositionMs = position,
            isMinimized = isMinimized
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayerUiState())

    fun onEvent(event: PlayerUiEvent) {
        when (event) {
            PlayerUiEvent.OnPlayPauseClick -> {
                if (musicPlayer.isPlaying.value) {
                    musicPlayer.pause()
                } else {
                    musicPlayer.resume()
                }
            }
            PlayerUiEvent.OnNextClick -> {
                musicPlayer.next()
            }
            PlayerUiEvent.OnPreviousClick -> {
                musicPlayer.previous()
            }
            PlayerUiEvent.OnFavoriteToggle -> {
                // Implementation for favorite
            }
            PlayerUiEvent.OnLoopToggle -> {
                val nextMode = if (musicPlayer.repeatMode.value == Player.REPEAT_MODE_OFF) {
                    Player.REPEAT_MODE_ALL
                } else {
                    Player.REPEAT_MODE_OFF
                }
                musicPlayer.setRepeatMode(nextMode)
            }
            PlayerUiEvent.OnMinimizeClick -> {
                _isMinimized.update { true }
            }
            PlayerUiEvent.OnMaximizeClick -> {
                _isMinimized.update { false }
            }
            is PlayerUiEvent.OnSeek -> {
                musicPlayer.seekTo(event.positionMs)
            }
            is PlayerUiEvent.OnOptionClick -> {
                // Mock option selection
            }
        }
    }

    private fun AudioTrack.toUiModel() = MockTrack(
        id = this.id,
        title = this.title ?: "Unknown",
        artist = this.artist ?: "Unknown",
        album = this.album ?: "Unknown",
        coverArtUrl = getCoverUriString(File(this.dataPath).parent ?: ""),
        durationMs = this.durationMs
    )

    private fun getCoverUriString(folderPath: String?): String? {
        if (folderPath.isNullOrBlank()) return null

        val directory = java.io.File(folderPath)
        if (!directory.exists() || !directory.isDirectory) return null

        val commonNames = listOf("cover.jpg", "cover.png", "folder.jpg", "folder.png")
        for (name in commonNames) {
            val file = java.io.File(directory, name)
            if (file.exists()) {
                return android.net.Uri.fromFile(file).toString()
            }
        }

        val validExtensions = listOf("jpg", "jpeg", "png")
        val imageFile = directory.listFiles()?.firstOrNull { file ->
            file.isFile && file.extension.lowercase() in validExtensions
        }

        if (imageFile != null) {
            return android.net.Uri.fromFile(imageFile).toString()
        }

        return null
    }
}
