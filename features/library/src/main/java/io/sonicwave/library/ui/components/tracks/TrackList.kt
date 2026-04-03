package io.sonicwave.library.ui.components.tracks

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.sonicwave.library.ui.LibraryScreen.TrackUiModel

@Composable
fun TrackList(
    audioFiles: List<TrackUiModel>,
    listState: LazyListState = rememberLazyListState(),
    onTrackClick: (TrackUiModel) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = audioFiles,
            key = { track -> track.id }
        ) { track ->
            TrackItem(
                track = track,
                coverUriString = track.coverUri,
                onClick = { onTrackClick(track) }
            )
        }
    }
}