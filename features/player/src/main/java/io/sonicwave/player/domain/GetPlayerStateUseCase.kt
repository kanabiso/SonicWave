package io.sonicwave.player.domain

import io.sonicwave.player.ui.MockTrack
import io.sonicwave.player.ui.PlayerUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetPlayerStateUseCase @Inject constructor() {
    operator fun invoke(): Flow<PlayerUiState> {
        return flowOf(
            PlayerUiState(
                track = MockTrack(
                    title = "Echoes of the Void",
                    artist = "Sonic Voyager",
                    album = "Stellar Drift",
                    coverArtUrl = null,
                    durationMs = 245000L
                ),
                isPlaying = false,
                isFavorite = false,
                isLooping = false,
                currentPositionMs = 0L,
                isMinimized = false
            )
        )
    }
}