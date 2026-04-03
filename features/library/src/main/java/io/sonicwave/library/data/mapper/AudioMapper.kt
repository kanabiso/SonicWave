package io.sonicwave.library.data.mapper

import android.content.ContentUris
import android.database.Cursor
import android.provider.MediaStore
import io.sonicwave.media.domain.model.AudioTrack

fun Cursor.toAudioTrack(): AudioTrack {
    val idColumn = getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
    val titleColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
    val artistColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
    val albumColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
    val durationColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
    val albumIdColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
    val trackColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
    val yearColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
    val dateAddedColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
    val dataColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

    val id = getLong(idColumn)

    return AudioTrack(
        id = id,
        title = getString(titleColumn),
        artist = getString(artistColumn),
        album = getString(albumColumn),
        uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id).toString(),
        durationMs = getLong(durationColumn),
        albumId = getLong(albumIdColumn),
        track = getInt(trackColumn),
        year = getString(yearColumn),
        dateAdded = getString(dateAddedColumn),
        dataPath = getString(dataColumn)
    )
}