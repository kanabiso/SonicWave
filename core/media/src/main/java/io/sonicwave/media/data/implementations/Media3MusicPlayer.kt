package io.sonicwave.media.data.implementations

import android.content.Context
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import io.sonicwave.media.domain.interfaces.MusicPlayer
import io.sonicwave.media.domain.service.PlaybackService
import io.sonicwave.media.model.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Media3MusicPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) : MusicPlayer, Player.Listener {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .build()

    val player: ExoPlayer = ExoPlayer.Builder(context)
        .setAudioAttributes(audioAttributes, true)
        .build().apply {
            addListener(this@Media3MusicPlayer)
        }

    private val _currentTrack = MutableStateFlow<AudioTrack?>(null)
    override val currentTrack: StateFlow<AudioTrack?> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _shuffleModeEnabled = MutableStateFlow(false)
    override val shuffleModeEnabled: StateFlow<Boolean> = _shuffleModeEnabled.asStateFlow()

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    override val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    init {
        startProgressUpdate()
    }

    override fun play(track: AudioTrack) {
        val mediaItem = MediaItem.Builder()
            .setMediaId(track.id.toString())
            .setUri(track.uri)
            .build()
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
        _currentTrack.value = track

        // Ensure service is started for background playback
        val intent = Intent(context, PlaybackService::class.java)
        context.startService(intent)
    }

    override fun pause() {
        player.pause()
    }

    override fun resume() {
        player.play()
    }

    override fun seekTo(position: Long) {
        player.seekTo(position)
        _currentPosition.value = position
    }

    override fun next() {
        player.seekToNext()
    }

    override fun previous() {
        player.seekToPrevious()
    }

    override fun setShuffleMode(enabled: Boolean) {
        player.shuffleModeEnabled = enabled
    }

    override fun setRepeatMode(repeatMode: Int) {
        player.repeatMode = repeatMode
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        _isPlaying.value = player.playWhenReady && player.playbackState != Player.STATE_ENDED
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        _isPlaying.value = player.playWhenReady && player.playbackState != Player.STATE_ENDED
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        _shuffleModeEnabled.value = shuffleModeEnabled
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        _repeatMode.value = repeatMode
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        // Update current track if needed when playlist changes
    }

    private fun startProgressUpdate() {
        scope.launch {
            while (isActive) {
                if (isPlaying.value) {
                    _currentPosition.value = player.currentPosition
                }
                delay(500)
            }
        }
    }
}
