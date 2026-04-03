package io.sonicwave.library.ui.albumScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.sonicwave.library.R
import io.sonicwave.library.ui.components.AlbumCoverImage
import io.sonicwave.library.ui.components.tracks.TrackList

@Composable
fun AlbumScreenRoot(
    albumId: Long,
    modifier: Modifier = Modifier,
    viewModel: AlbumViewModel = hiltViewModel(key = albumId.toString())
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(albumId) {
        viewModel.loadAlbum(albumId)
    }

    AlbumScreen(
        modifier = modifier,
        uiState = state,
        onTrackClick = {
            viewModel.onTrackClick(it)
        }
    )
}

@Composable
fun AlbumScreen(
    modifier: Modifier = Modifier,
    uiState: AlbumUiState,
    onTrackClick: (Long) -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(16.dp).fillMaxSize()
    ) {

        AlbumCoverImage(
            coverUriString = uiState.coverUri,
            defaultCover = R.drawable.music_note_2_300dp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(200.dp)
                .padding(vertical = 8.dp)
        )

        Text(
            text = uiState.name,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = MaterialTheme.typography.displaySmall.fontSize,
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = uiState.artist,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                ),
        )

        Spacer(Modifier.height(16.dp))

        TrackList(
            audioFiles = uiState.tracks
        ) {
            onTrackClick(it.id)
        }

    }

}