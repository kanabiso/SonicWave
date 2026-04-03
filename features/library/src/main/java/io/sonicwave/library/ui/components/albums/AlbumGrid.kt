package io.sonicwave.library.ui.components.albums

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.sonicwave.library.R
import io.sonicwave.library.ui.LibraryScreen.AlbumUiModel


@Composable
fun AlbumGrid(
    albums: List<AlbumUiModel>,
    gridState: LazyGridState = rememberLazyGridState(),
    onAlbumClick: (Long) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(1.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = albums,
            key = { it.id }
        ) { album ->
            AlbumItem(
                name = album.name ?: stringResource(R.string.unknown_album),
                artist = album.artist ?: stringResource(R.string.unknown_artist),
                year = album.year,
                coverUriString = album.coverUri,
                duration = album.duration,
                onClick = { onAlbumClick(album.id) },
            )
        }
    }
}

@Preview(
    showBackground = true,
//    showSystemUi = true
)
@Composable
fun AlbumGridPreview() {
    AlbumGrid(
        gridState = rememberLazyGridState(),
        albums = listOf(
            AlbumUiModel(
                id = 1,
                name = "Latarnie dawno wszędzie zgasły",
                artist = "Taco Hemingway",
                year = "2025",
                trackCount = 10,
                duration = "4:20",
                coverUri = "null"
            ),
            AlbumUiModel(
                id = 1,
                name = "Your Papers Please",
                artist = "Taco Hemingway",
                year = "2025",
                trackCount = 10,
                duration = "4:20",
                coverUri = "null",
            )
        ),
        onAlbumClick = {}
    )
}