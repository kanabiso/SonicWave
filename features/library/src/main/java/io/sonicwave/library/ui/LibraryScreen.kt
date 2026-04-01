package io.sonicwave.library.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.sonicwave.library.R
import io.sonicwave.library.ui.components.albums.AlbumGrid
import io.sonicwave.library.ui.components.albums.AlbumList
import io.sonicwave.library.ui.components.FilterRow
import io.sonicwave.library.ui.components.LibraryTitle
import io.sonicwave.library.ui.components.RequireAudioPermission
import io.sonicwave.library.ui.components.SortSheet
import io.sonicwave.library.ui.components.tracks.TrackGrid
import io.sonicwave.library.ui.components.tracks.TrackList

@Composable
fun LibraryScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val libraryItems = viewModel.libraryItems.collectAsStateWithLifecycle().value

    RequireAudioPermission {
        LaunchedEffect(Unit) {
            viewModel.onEvent(LibraryUiEvent.OnPermissionsGranted)
        }

        LibraryScreen(
            uiState = uiState,
            libraryItems = libraryItems,
            onEvent = viewModel::onEvent,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    libraryItems: List<LibraryItemUiModel>,
    uiState: LibraryUiState,
    onEvent: (LibraryUiEvent) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()

    LaunchedEffect(uiState.sortOrder, uiState.isDesc) {
        listState.scrollToItem(0)
        gridState.scrollToItem(0)
    }

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        LibraryTitle(
            text = if (uiState.isAlbumGroup) stringResource(R.string.albums) else stringResource(R.string.your_tarcks),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        FilterRow(
            isListLayout = if (uiState.isAlbumGroup) uiState.isAlbumListLayout else uiState.isTracksListLayout,
            isAlbumGroup = uiState.isAlbumGroup,
            onFilterClick = { showBottomSheet = true },
            onAlbumLayoutClick = { onEvent(LibraryUiEvent.OnAlbumLayoutToggleClick) },
            onTracksLayoutClick = { onEvent(LibraryUiEvent.OnTracksLayoutToggleClick) },
            onAlbumClick = {
                onEvent(LibraryUiEvent.OnAlbumIconClick) }
        )

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                libraryItems.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.no_audio_files_found))
                    }
                }

                uiState.errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(uiState.errorMessage)
                    }
                }
                else -> {
                    if (uiState.isAlbumGroup) {

                        val albums = libraryItems.filterIsInstance<AlbumUiModel>()

                        if (uiState.isAlbumListLayout) {
                            AlbumList(albums = albums, listState = listState) { album ->
                                // Navigation to album details could be added here
                            }
                        } else {
                            AlbumGrid(albums = albums, gridState = gridState) { album ->
                                // Navigation to album details could be added here
                            }
                        }

                    } else {
                        val tracks = libraryItems.filterIsInstance<TrackUiModel>()

                        if (uiState.isTracksListLayout) {
                            TrackList(audioFiles = tracks, listState = listState) { track ->
                                onEvent(LibraryUiEvent.OnTrackClick(track.id))
                            }
                        } else {
                            TrackGrid(audioFiles = tracks, gridState = gridState) { track ->
                                onEvent(LibraryUiEvent.OnTrackClick(track.id))
                            }
                        }
                    }
                }
            }

        if (showBottomSheet) {
            SortSheet(
                sheetState = sheetState,
                onSheetOpen = { showBottomSheet = it },
                sortOrder = uiState.sortOrder,
                isDesc = uiState.isDesc,
                isAlbumGroup = uiState.isAlbumGroup,
                scope = scope,
                onEvent = onEvent
            )
        }
    }
}
