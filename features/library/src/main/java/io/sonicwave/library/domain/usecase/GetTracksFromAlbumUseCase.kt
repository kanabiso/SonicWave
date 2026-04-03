package io.sonicwave.library.domain.usecase

import io.sonicwave.library.domain.repository.AudioRepository
import io.sonicwave.media.domain.model.AudioTrack
import jakarta.inject.Inject

class GetTracksFromAlbumUseCase @Inject constructor(
    private val audioRepository: AudioRepository
) {
    operator fun invoke(albumId: Long): List<AudioTrack> {
        return audioRepository.getAudioFilesFromAlbum(albumId)
    }
}