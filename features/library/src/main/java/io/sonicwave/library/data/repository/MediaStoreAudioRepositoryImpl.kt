package io.sonicwave.library.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import io.sonicwave.library.data.mapper.toAudioTrack
import io.sonicwave.library.domain.repository.AudioRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import androidx.core.net.toUri
import io.sonicwave.media.domain.model.AudioTrack

class MediaStoreAudioRepositoryImpl @Inject constructor(
    private val context: Context
) : AudioRepository {
    private val contentResolver = context.contentResolver

    override suspend fun getAlbumArtData(uriString: String): ByteArray? = withContext(Dispatchers.IO) {
        val retriever = android.media.MediaMetadataRetriever()
        return@withContext try {
            retriever.setDataSource(context, Uri.parse(uriString))
            retriever.embeddedPicture
        } catch (e: Exception) {
            null
        } finally {
            retriever.release()
        }
    }

    override suspend fun getAlbumArtUri(albumId: Long): Uri? = withContext(Dispatchers.IO) {
        val artworkUri = "content://media/external/audio/albumart".toUri()
        val uri = ContentUris.withAppendedId(artworkUri, albumId)

        return@withContext try {
            contentResolver.openInputStream(uri)?.close()
            uri
        } catch (e: Exception) {
            null
        }
    }

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
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
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

            while (cursor.moveToNext()) {
                audioList.add(
                    cursor.toAudioTrack()
                )
            }
        }
        return audioList
    }

    override suspend fun getTrackById(id: Long): AudioTrack? = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )

        val selection = "${MediaStore.Audio.Media._ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                return@withContext cursor.toAudioTrack()
            }
        }

        return@withContext null
    }
}