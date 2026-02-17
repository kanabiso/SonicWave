package io.sonicwave.library.domain.usecase

import io.sonicwave.media.domain.interfaces.MusicPlayer
import jakarta.inject.Inject

class PlayTrackUseCase @Inject constructor(
    private val musicPlayer: MusicPlayer
) {
    operator fun invoke(trackId: String) {
        musicPlayer.play(trackId)
    }
}