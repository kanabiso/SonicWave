package io.sonicwave.player.ui

import io.sonicwave.player.ui.model.PlayerOptions

sealed class PlayerUiEvent {
    object OnPlayPauseClick : PlayerUiEvent()
    object OnNextClick : PlayerUiEvent()
    object OnPreviousClick : PlayerUiEvent()
    object OnFavoriteToggle : PlayerUiEvent()
    object OnLoopToggle : PlayerUiEvent()
    object OnMinimizeClick : PlayerUiEvent()
    object OnMaximizeClick : PlayerUiEvent()
    data class OnSeek(val positionMs: Long) : PlayerUiEvent()
    data class OnOptionClick(val option: PlayerOptions) : PlayerUiEvent()
}