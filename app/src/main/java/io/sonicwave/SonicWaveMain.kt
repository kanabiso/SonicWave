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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import io.sonicwave.library.ui.albumScreen.AlbumScreenRoot
import io.sonicwave.library.ui.LibraryScreen.LibraryScreenRoot
import io.sonicwave.navigation.Route
import io.sonicwave.player.ui.PlayerScreenRoot
import io.sonicwave.player.ui.PlayerUiEvent
import io.sonicwave.player.ui.PlayerViewModel
import io.sonicwave.player.ui.components.MiniPlayerBar
import kotlin.collections.listOf

@Composable
fun SonicWaveMain() {
    val viewModel: PlayerViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    val backStack = rememberNavBackStack(Route.Library)

    val saveableStateDecorator = rememberSaveableStateHolderNavEntryDecorator<Any>()
    val viewModelStoreDecorator = rememberViewModelStoreNavEntryDecorator<Any>()

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

            NavDisplay (
                entryDecorators = remember(saveableStateDecorator, viewModelStoreDecorator) {
                    listOf(saveableStateDecorator, viewModelStoreDecorator)
                },
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = { key ->
                    when (key) {
                        is Route.Library -> NavEntry(key) {
                            LibraryScreenRoot(
                                modifier = Modifier.fillMaxSize(),
                                onAlbumClick = { albumId ->
                                    backStack.add(Route.Album(albumId))
                                }
                            )
                        }

                        is Route.Album -> NavEntry(key) {
                            AlbumScreenRoot(
                                albumId = key.albumId,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        else -> error("Invalid route $key")
                    }
                }
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