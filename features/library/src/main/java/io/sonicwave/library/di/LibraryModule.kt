package io.sonicwave.library.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.sonicwave.library.data.repository.MediaStoreAudioRepositoryImpl
import io.sonicwave.library.domain.repository.AudioRepository
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object LibraryModule {

    @Provides
    @Singleton
    fun provideAudioRepository(
        @ApplicationContext context: Context
    ): AudioRepository {
        return MediaStoreAudioRepositoryImpl(context.contentResolver)
    }
}