package io.sonicwave.library.domain.usecase

import io.sonicwave.library.domain.repository.PreferenceRepository
import jakarta.inject.Inject

class SetTracksLayoutPreferenceUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {
    suspend operator fun invoke(isList: Boolean) {
        preferenceRepository.setTracksListLayout(isList)
    }
}
