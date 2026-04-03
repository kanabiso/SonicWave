package io.sonicwave.library.ui.mappers

import android.content.ContentUris
import android.net.Uri
import io.sonicwave.common.utils.formatAsDuration
import io.sonicwave.library.ui.LibraryScreen.AlbumUiModel
import io.sonicwave.library.ui.LibraryScreen.TrackUiModel
import io.sonicwave.media.domain.model.AudioAlbum
import io.sonicwave.media.domain.model.AudioTrack
import java.io.File
import androidx.core.net.toUri

fun AudioTrack.toUiModel(isPlaying: Boolean): TrackUiModel {

    return TrackUiModel(
        id = this.id,
        title = this.title,
        artist = this.artist,
        album = this.album,
        year = this.year,
        track = this.track,
        albumId = this.albumId,
        duration = this.durationMs.formatAsDuration(),
        isPlaying = isPlaying,
        coverUri = getCoverUri(this.albumId)
    )
}

fun AudioAlbum.toUiModel(): AlbumUiModel {
    return AlbumUiModel(
        id = this.albumId,
        name = this.name,
        artist = this.artist,
        year = this.year,
        duration = this.durationMs?.formatAsDuration() ?: "",
        trackCount = this.trackCount,
        coverUri = getCoverUri(this.albumId)
    )
}

private fun getCoverUri(albumId : Long): String{
    val artworkUri = "content://media/external/audio/albumart".toUri()
    val albumCoverUri = ContentUris.withAppendedId(artworkUri, albumId)

    return albumCoverUri.toString()
}
