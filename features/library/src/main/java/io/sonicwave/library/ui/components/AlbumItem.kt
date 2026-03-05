package io.sonicwave.library.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AlbumItem(
    name: String,
    artist: String,
    year: String,
    duration: String,
    coverUriString: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = MaterialTheme.shapes.large,
        modifier = modifier
            .fillMaxWidth(0.5f)
            .fillMaxHeight(0.3f)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            AlbumCoverImage(coverUriString = coverUriString)

            Text(
                text = name,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
            Text(
                text = artist,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
            )
            Row(
                horizontalArrangement = Arrangement.Center,
            ) {
                if (year.isNotBlank()) {
                    Text(
                        text = "$year | ",
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 10.sp
                    )
                }
                Text(
                    text = duration,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 10.sp
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