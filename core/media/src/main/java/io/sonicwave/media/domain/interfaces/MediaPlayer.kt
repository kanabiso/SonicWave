package io.sonicwave.media.domain.interfaces

import io.sonicwave.media.domain.model.AudioTrack
import kotlinx.coroutines.flow.StateFlow

interface MusicPlayer {
    val currentTrack: StateFlow<AudioTrack?>
    val isPlaying: StateFlow<Boolean>
    val currentPosition: StateFlow<Long>
    val shuffleModeEnabled: StateFlow<Boolean>
    val repeatMode: StateFlow<Int>

    fun play(track: AudioTrack)
    fun pause()
    fun resume()
    fun seekTo(position: Long)
    fun next()
    fun previous()
    fun setShuffleMode(enabled: Boolean)
    fun setRepeatMode(repeatMode: Int)
    }