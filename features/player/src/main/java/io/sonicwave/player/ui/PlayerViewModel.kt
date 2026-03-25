package io.sonicwave.player.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.sonicwave.player.domain.GetPlayerStateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val getPlayerStateUseCase: GetPlayerStateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        getPlayerStateUseCase()
            .onEach { state ->
                _uiState.update { state }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: PlayerUiEvent) {
        when (event) {
            PlayerUiEvent.OnPlayPauseClick -> {
                _uiState.update { it.copy(isPlaying = !it.isPlaying) }
            }
            PlayerUiEvent.OnNextClick -> {
                // Mock next track
            }
            PlayerUiEvent.OnPreviousClick -> {
                // Mock previous track
            }
            PlayerUiEvent.OnFavoriteToggle -> {
                _uiState.update { it.copy(isFavorite = !it.isFavorite) }
            }
            PlayerUiEvent.OnLoopToggle -> {
                _uiState.update { it.copy(isLooping = !it.isLooping) }
            }
            PlayerUiEvent.OnMinimizeClick -> {
                _uiState.update { it.copy(isMinimized = true) }
            }
            PlayerUiEvent.OnMaximizeClick -> {
                _uiState.update { it.copy(isMinimized = false) }
            }
            is PlayerUiEvent.OnSeek -> {
                _uiState.update { it.copy(currentPositionMs = event.positionMs) }
            }
            is PlayerUiEvent.OnOptionClick -> {
                // Mock option selection
            }
        }
    }
}