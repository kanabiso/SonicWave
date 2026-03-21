package io.sonicwave.library.ui.components.albums

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.sonicwave.library.ui.AlbumUiModel

@Composable
fun AlbumList(
    albums: List<AlbumUiModel>,
    listState: LazyListState = rememberLazyListState(),
    onAlbumClick: (AlbumUiModel) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = albums,
            key = { album -> album.id }
        ) { album ->
            AlbumListItem(
                album = album,
                onClick = { onAlbumClick(album) }
            )
        }
    }
}
