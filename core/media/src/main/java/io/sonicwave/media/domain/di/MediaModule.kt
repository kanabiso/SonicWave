package io.sonicwave.media.domain.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.sonicwave.media.domain.implementations.Media3MusicPlayer
import io.sonicwave.media.domain.interfaces.MusicPlayer
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class MediaModule {

    @Binds
    @Singleton
    abstract fun bindMusicPlayer(
        media3MusicPlayer: Media3MusicPlayer
    ): MusicPlayer
}