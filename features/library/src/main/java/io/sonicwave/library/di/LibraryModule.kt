package io.sonicwave.library.di

import android.content.Context
import io.sonicwave.library.domain.repository.AudioRepository


@Module
@InstallIn(SingletonComponent::class)
internal object LibraryModule {

    @Provides
    @Singleton
    fun provideAudioRepository(
        @ApplicationContext context: Context
    ): AudioRepository {
        // Dostarczamy konkretną implementację, wstrzykując potrzebne zależności systemowe
        return MediaStoreAudioRepository(context.contentResolver)
    }
}