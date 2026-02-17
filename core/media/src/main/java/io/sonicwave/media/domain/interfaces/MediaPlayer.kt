package io.sonicwave.media.domain.interfaces

//import android.media.AudioTrack
import io.sonicwave.media.model.AudioTrack
import kotlinx.coroutines.flow.StateFlow

interface MusicPlayer {
    val currentTrack: StateFlow<AudioTrack?>
    val isPlaying: StateFlow<Boolean>
    val currentPosition: StateFlow<Long>

    fun play(track: AudioTrack)
    fun pause()
    fun resume()
    fun seekTo(position: Long)
}