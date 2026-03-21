package io.sonicwave.library.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import io.sonicwave.library.R

@Composable
fun FilterRow(
    isListLayout: Boolean,
    isAlbumGroup: Boolean,
    onFilterClick: () -> Unit,
    onAlbumLayoutClick: () -> Unit,
    onTracksLayoutClick: () -> Unit,
    onAlbumClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row {
            IconButton(
                onClick = { onFilterClick() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.filter_list),
                    contentDescription = "Filter"
                )
            }
            IconButton(
                onClick = { onAlbumClick() }
            ) {
                Icon(
                    painter = painterResource(id = if (isAlbumGroup) R.drawable.filled_album_24 else R.drawable.album_24 ),
                    contentDescription = "Group by Album",
                )
            }
        }
        IconButton(
            onClick = {
                if (isAlbumGroup) {
                    onAlbumLayoutClick()
                } else {
                    onTracksLayoutClick()
                }
            }
        ) {
            Icon(
                painter = painterResource(id =
                    if (isListLayout) R.drawable.border_all_24 else R.drawable.density_small_24),
                contentDescription = if (isListLayout) "Switch to Grid View" else "Switch to List View"
            )
        }
    }
}