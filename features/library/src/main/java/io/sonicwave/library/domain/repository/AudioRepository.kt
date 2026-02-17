package io.sonicwave.library.domain.repository

import io.sonicwave.media.model.AudioTrack

//import io.sonicwave.library.domain.model.AudioTrack

interface AudioRepository {
    suspend fun getAudioFiles(): List<AudioTrack>
}