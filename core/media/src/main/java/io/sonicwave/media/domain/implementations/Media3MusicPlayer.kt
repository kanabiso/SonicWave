package io.sonicwave.media.domain.implementations

import io.sonicwave.media.domain.interfaces.MusicPlayer
import io.sonicwave.media.model.AudioTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class Media3MusicPlayer @Inject constructor() : MusicPlayer {
    private val _currentTrack = MutableStateFlow<AudioTrack?>(null)
    override val currentTrack: StateFlow<AudioTrack?> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    override fun play(trackId: String) {

    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun seekTo(position: Long) {

    }
}