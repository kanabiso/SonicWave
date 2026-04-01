package io.sonicwave.library.domain.repository

import android.net.Uri
import io.sonicwave.media.domain.model.AudioTrack
import kotlinx.coroutines.flow.Flow


interface AudioRepository {
    fun getAudioFiles(): Flow<List<AudioTrack>>
    suspend fun getAlbumArtUri(albumId: Long): Uri?
    suspend fun getAlbumArtData(uriString: String): ByteArray?
    suspend fun getTrackById(id: Long): AudioTrack?
}