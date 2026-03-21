package io.sonicwave.library.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import io.sonicwave.library.R

@Composable
fun AlbumCoverImage(
    coverUriString: String,
    modifier: Modifier = Modifier,
    defaultCover: Int = R.drawable.music_note_2_300dp
) {
    val context = LocalContext.current

    val imageRequest = remember(coverUriString) {
        ImageRequest.Builder(context)
            .data(coverUriString)
            .crossfade(false)
            .size(coil.size.Size.ORIGINAL)
            .build()
    }

    val painter = rememberAsyncImagePainter(model = imageRequest)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        when (painter.state) {
            is AsyncImagePainter.State.Loading -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxSize(0.4f),
                    strokeWidth = 3.dp
                )
            }
            is AsyncImagePainter.State.Error,
            is AsyncImagePainter.State.Empty -> {
                Icon(
                    painter = painterResource(id = defaultCover),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxSize(0.6f)
                )
            }
            else -> {
            }
        }
    }
}