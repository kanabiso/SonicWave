package io.sonicwave.library.ui.components.albums

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.sonicwave.library.R
import io.sonicwave.library.ui.components.AlbumCoverImage

@Composable
fun AlbumItem(
    name: String,
    artist: String,
    year: String?,
    duration: String,
    coverUriString: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = MaterialTheme.shapes.large,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick)
        ) {
            AlbumCoverImage(
                coverUriString = coverUriString,
                defaultCover = R.drawable.music_note_2_300dp,
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth()
                    .padding(top = 8.dp, end = 8.dp, start = 8.dp, bottom = 0.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 12.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = artist,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (year != null) "$year • $duration" else duration,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun AlbumItemPreview() {
//    AlbumItem(
//        name = "Latarnie dawno wszędzie zgasły",
//        artist = "Taco Hemingway",
//        year = "2025",
//        duration = "4:20"
//
//    )
//}