package io.sonicwave.library.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.sonicwave.library.ui.TrackUiModel

@Composable
fun TrackList(
    audioFiles: List<TrackUiModel>,
    onTrackClick: (TrackUiModel) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = audioFiles,
            key = { track -> track.id }
        ) { track ->
            TrackItem(
                track = track,
                onClick = { onTrackClick(track) }
            )
        }
    }
}