package io.sonicwave.library.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import io.sonicwave.library.domain.repository.AudioRepository
import io.sonicwave.media.model.AudioTrack
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn


class MediaStoreAudioRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver
) : AudioRepository {

    override fun getAudioFiles(): Flow<List<AudioTrack>> = callbackFlow {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                trySend(fetchAudioTracksFromCursor())
            }
        }

        contentResolver.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            true,
            observer
        )

        trySend(fetchAudioTracksFromCursor())

        awaitClose {
            contentResolver.unregisterContentObserver(observer)
        }
    }.flowOn(Dispatchers.IO)

    private fun fetchAudioTracksFromCursor(): List<AudioTrack> {
        val audioList = mutableListOf<AudioTrack>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val trackColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                audioList.add(
                    AudioTrack(
                        id = id.toString(),
                        title = cursor.getString(titleColumn) ?: "Unknown Title",
                        artist = cursor.getString(artistColumn) ?: "Unknown Artist",
                        album = cursor.getString(albumColumn) ?: "Unknown Album",
                        uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id).toString(),
                        durationMs = cursor.getLong(durationColumn),
                        albumId = cursor.getLong(albumIdColumn).toString(),
                        track = cursor.getString(trackColumn) ?: "",
                        year = cursor.getString(yearColumn) ?: "",
                        dateAdded = cursor.getString(dateAddedColumn)
                    )
                )
            }
        }
        return audioList
    }
}

//class MediaStoreAudioRepositoryImpl @Inject constructor(
//    private val contentResolver: ContentResolver
//) : AudioRepository {
//
//    override fun getAudioFiles(): Flow<List<AudioTrack>> = flow {
//        val audioList = mutableListOf<AudioTrack>()
//
//        // Columns
//        val projection = arrayOf(
//            MediaStore.Audio.Media._ID,
//            MediaStore.Audio.Media.TITLE,
//            MediaStore.Audio.Media.ALBUM,
//            MediaStore.Audio.Media.ALBUM_ID,
//            MediaStore.Audio.Media.TRACK,
//            MediaStore.Audio.Media.YEAR,
//            MediaStore.Audio.Media.DATE_ADDED,
//            MediaStore.Audio.Media.ARTIST,
//            MediaStore.Audio.Media.DURATION
//        )
//
//        // only music files
//        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
//
//        // sorting
//        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
//
//        // read system database
//        val cursor = contentResolver.query(
//            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//            projection,
//            selection,
//            null,
//            sortOrder
//        )
//
//        // map to AudioTrack model
//        cursor?.use {
//            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
//            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
//            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
//            val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
//            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
//            val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
//            val trackColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
//            val yearColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
//            val dateAddedColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
//
//            while (it.moveToNext()) {
//                val id = it.getLong(idColumn)
//                val title = it.getString(titleColumn) ?: "Unknown Title"
//                val artist = it.getString(artistColumn) ?: "Unknown Artist"
//                val album = it.getString(albumColumn) ?: "Unknown Album"
//                val duration = it.getLong(durationColumn)
//                val albumId = it.getLong(albumIdColumn)
//                val track = it.getString(trackColumn)
//                val year = it.getString(yearColumn)
//                val dateAdded = it.getString(dateAddedColumn)
//
//                // generate uri
//                val contentUri = ContentUris.withAppendedId(
//                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                    id
//                ).toString()
//
//                audioList.add(
//                    AudioTrack(
//                        id = id.toString(),
//                        title = title,
//                        artist = artist,
//                        album = album,
//                        uri = contentUri,
//                        durationMs = duration,
//                        albumId = albumId.toString(),
//                        track = track ?: "",
//                        year = year ?: "",
//                        dateAdded = dateAdded
//                    )
//                )
//            }
//        }
//        emit(audioList)
//    }.flowOn(Dispatchers.IO)
//}