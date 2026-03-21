package io.sonicwave.library.domain.usecase

import io.sonicwave.library.domain.repository.PreferenceRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAlbumLayoutPreferenceUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {
    operator fun invoke(): Flow<Boolean> = preferenceRepository.isAlbumListLayout
}
