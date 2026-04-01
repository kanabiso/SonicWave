package io.sonicwave.library.domain.usecase

import io.sonicwave.library.domain.repository.AudioRepository
import io.sonicwave.media.domain.interfaces.MusicPlayer
import io.sonicwave.media.domain.model.AudioTrack
import jakarta.inject.Inject

class PlayTrackUseCase @Inject constructor(
    private val musicPlayer: MusicPlayer,
    private val audioRepository: AudioRepository
) {
    suspend operator fun invoke(trackId: Long) {
        // Pobieramy model domeny na podstawie ID
        val track = audioRepository.getTrackById(trackId)

        if (track != null) {
            musicPlayer.play(track)
        }
    }
}