package io.sonicwave.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.sonicwave.player.ui.components.PlaybackControl
import io.sonicwave.player.ui.components.PlayerTopBar
import io.sonicwave.player.ui.components.ProgressBar
import io.sonicwave.player.ui.components.TrackPlayerInfo
import io.sonicwave.player.ui.components.TrackVisual

@Composable
fun PlayerScreenRoot(
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    PlayerScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun PlayerScreen(
    state: PlayerUiState,
    onEvent: (PlayerUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PlayerTopBar(
            onMinimizeClick = { onEvent(PlayerUiEvent.OnMinimizeClick) },
            onMoreOptionsClick = { onEvent(PlayerUiEvent.OnOptionClick(it)) }
        )

        Spacer(modifier = Modifier.weight(0.5f))

        // Cover Art or Wave Animation
        TrackVisual(
            isPlaying = state.isPlaying,
            coverArtUrl = state.track.coverArtUrl
        )

        Spacer(modifier = Modifier.weight(1f))

        TrackPlayerInfo(
            title = state.track.title,
            artist = state.track.artist,
            album = state.track.album
        )

        Spacer(modifier = Modifier.height(32.dp))

        ProgressBar(
            currentPositionMs = state.currentPositionMs,
            durationMs = state.track.durationMs
        ) {
            onEvent(PlayerUiEvent.OnSeek(it))
        }

        Spacer(modifier = Modifier.height(32.dp))

        PlaybackControl(
            isPlaying = state.isPlaying,
            isFavorite = state.isFavorite,
            isLooping = state.isLooping,
            onPlayPauseClick = { onEvent(PlayerUiEvent.OnPlayPauseClick) },
            onFavoriteToggle = { onEvent(PlayerUiEvent.OnFavoriteToggle) },
            onLoopToggle = { onEvent(PlayerUiEvent.OnLoopToggle) },
            onPreviousClick = { onEvent(PlayerUiEvent.OnPreviousClick) },
            onNextClick = { onEvent(PlayerUiEvent.OnNextClick) }
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}





