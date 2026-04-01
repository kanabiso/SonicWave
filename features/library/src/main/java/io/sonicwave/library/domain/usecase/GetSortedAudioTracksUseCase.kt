package io.sonicwave.library.domain.usecase

import io.sonicwave.library.domain.model.SortOrder
import io.sonicwave.library.domain.repository.AudioRepository
import io.sonicwave.media.domain.model.AudioTrack
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetSortedAudioTracksUseCase @Inject constructor(
    private val repository: AudioRepository
) {
    operator fun invoke(
        sortOrderFlow: Flow<SortOrder>,
        isDescFlow: Flow<Boolean>
    ): Flow<List<AudioTrack>> {
        return combine(
            repository.getAudioFiles(),
            sortOrderFlow,
            isDescFlow
        ) { tracks, sortOrder, isDesc ->

            when (sortOrder) {
                SortOrder.TITLE -> if (isDesc) tracks.sortedByDescending { it.title } else tracks.sortedBy { it.title }
                SortOrder.AUTHOR -> if (isDesc) tracks.sortedByDescending { it.artist } else tracks.sortedBy { it.artist }
                SortOrder.DURATION -> if (isDesc) tracks.sortedByDescending { it.durationMs } else tracks.sortedBy { it.durationMs }
                SortOrder.LAST_ADDED -> if (isDesc) tracks.sortedByDescending { it.dateAdded } else tracks.sortedBy { it.dateAdded }
            }
        }
    }
}