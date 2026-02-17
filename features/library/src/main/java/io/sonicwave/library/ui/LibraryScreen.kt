package io.sonicwave.library.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.sonicwave.library.R
import io.sonicwave.library.ui.components.FilterRow
import io.sonicwave.library.ui.components.LibraryTitle
import io.sonicwave.library.ui.components.RequireAudioPermission
import io.sonicwave.library.ui.components.TrackGrid
import io.sonicwave.library.ui.components.TrackList

@Composable
fun LibraryScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    RequireAudioPermission {
        LibraryScreen(
            uiState = uiState,
            onEvent = viewModel::onEvent,
            modifier = modifier
        )
    }
}

@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    uiState: LibraryUiState,
    onEvent: (LibraryUiEvent) -> Unit,
) {
    var isListLayout by rememberSaveable { mutableStateOf(true) }

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        LibraryTitle(modifier = Modifier.align(Alignment.CenterHorizontally))

        FilterRow(
            isListLayout = isListLayout,
            onFilterClick = { },
            onSortClick = { },
            onLayoutClick = { isListLayout = !isListLayout }
        )

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.audioFiles.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_audio_files_found))
                }
            }
            else -> {
                if (isListLayout) {
                    TrackList(uiState.audioFiles) { onEvent(LibraryUiEvent.OnTrackClick(it.id)) }
                } else {
                    TrackGrid(uiState.audioFiles) { onEvent(LibraryUiEvent.OnTrackClick(it.id)) }
                }
            }
        }
    }
}