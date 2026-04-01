package io.sonicwave.media.data.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionToken
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.sonicwave.media.data.implementations.Media3MusicPlayer
import io.sonicwave.media.domain.interfaces.MusicPlayer
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class MediaModule {

    @Binds
    @Singleton
    abstract fun bindMusicPlayer(
        media3MusicPlayer: Media3MusicPlayer
    ): MusicPlayer

    companion object {
        @Provides
        @Singleton
        fun provideMediaSession(
            @ApplicationContext context: Context,
            musicPlayer: Media3MusicPlayer
        ): MediaSession {
            return MediaSession.Builder(context, musicPlayer.player).build()
        }
        
        @Provides
        @Singleton
        fun provideSessionToken(mediaSession: MediaSession): SessionToken {
            return mediaSession.token
        }

        @Provides
        @Singleton
        fun provideExoPlayer(
            @ApplicationContext context: Context
        ): ExoPlayer {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build()

            return ExoPlayer.Builder(context)
                .setAudioAttributes(audioAttributes, true)
                .build()
        }
    }
}
