package io.sonicwave.media.domain.di

import io.sonicwave.media.domain.interfaces.MusicPlayer

@Module
@InstallIn(SingletonComponent::class)
internal abstract class MediaModule {

    // Używamy @Singleton, ponieważ stan odtwarzacza musi być
    // jeden i ten sam dla całej aplikacji (współdzielony między ekranami i serwisem).
    @Binds
    @Singleton
    abstract fun bindMusicPlayer(
        media3MusicPlayer: Media3MusicPlayer
    ): MusicPlayer
}