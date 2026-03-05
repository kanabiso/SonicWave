package io.sonicwave.library.domain.usecase

import io.sonicwave.library.domain.repository.AudioRepository
import jakarta.inject.Inject

class LoadAlbumCoversUri @Inject constructor(
    private val repository: AudioRepository
) {
    suspend operator fun invoke(uriString: String): ByteArray? {
        return repository.getAlbumArtData(uriString)
    }
}