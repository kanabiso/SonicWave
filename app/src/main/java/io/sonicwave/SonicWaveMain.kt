package io.sonicwave

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.sonicwave.library.ui.LibraryScreenRoot
import io.sonicwave.player.ui.PlayerScreenRoot
import io.sonicwave.player.ui.PlayerUiEvent
import io.sonicwave.player.ui.PlayerViewModel
import io.sonicwave.player.ui.components.MiniPlayerBar

@Composable
fun SonicWaveMain() {
    val viewModel: PlayerViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = state.isMinimized && state.track.id != 0L,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                MiniPlayerBar(
                    modifier = Modifier.navigationBarsPadding(),
                    state = state,
                    onEvent = viewModel::onEvent
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LibraryScreenRoot(
                modifier = Modifier.fillMaxSize()
            )

            AnimatedVisibility(
                visible = !state.isMinimized,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                BackHandler {
                    viewModel.onEvent(PlayerUiEvent.OnMinimizeClick)
                }
                PlayerScreenRoot(
                    viewModel = viewModel
                )
            }
        }
    }
}