package io.sonicwave.library.ui.components

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import io.sonicwave.library.ui.TrackUiModel

@Composable
fun TrackGrid(
    audioFiles: List<TrackUiModel>,
    gridState: LazyGridState = rememberLazyGridState(),
    onTrackClick: (TrackUiModel) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        state = gridState
    ) {
        items(
            items = audioFiles,
            key = { track -> track.id }
        ) { track ->
            TrackCell(
                track = track,
                onClick = { onTrackClick(track) }
            )
        }
    }
}