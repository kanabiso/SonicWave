package io.sonicwave.library.domain.repository

import io.sonicwave.media.model.AudioTrack
import kotlinx.coroutines.flow.Flow


interface AudioRepository {
    fun getAudioFiles(): Flow<List<AudioTrack>>
}