package io.sonicwave.library.domain.usecase


import io.sonicwave.library.domain.repository.AudioRepository
import io.sonicwave.media.model.AudioTrack
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAudioTracksUseCase @Inject constructor(
    private val repository: AudioRepository
) {
    operator fun invoke(): Flow<List<AudioTrack>> {

        return repository.getAudioFiles()
    }
}