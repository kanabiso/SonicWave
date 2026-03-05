package io.sonicwave.library.domain.usecase

import io.sonicwave.library.domain.model.SortOrder
import io.sonicwave.library.domain.repository.AudioRepository
import io.sonicwave.media.model.AudioAlbum
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.io.File

class GetSortedAlbumsUseCase @Inject constructor(
    private val repository: AudioRepository
) {
    operator fun invoke(
        sortOrderFlow: Flow<SortOrder>,
        isDescFlow: Flow<Boolean>
    ): Flow<List<AudioAlbum>> {
        return combine(
            repository.getAudioFiles(),
            sortOrderFlow,
            isDescFlow
        ) { tracks, sortOrder, isDesc ->

            val albums = tracks.groupBy { it.albumId }.map { (albumId, trackList) ->
                val firstTrack = trackList.first()
                AudioAlbum(
                    albumId = albumId,
                    name = firstTrack.album ?: "Unknown Album",
                    artist = firstTrack.artist,
                    year = firstTrack.year,
                    durationMs = trackList.sumOf { it.durationMs },
                    trackCount = trackList.size,
                    firstTrackUri = firstTrack.uri,
                    folderPath = File(firstTrack.dataPath).parent ?: ""
                )
            }

            when (sortOrder) {
                SortOrder.TITLE -> if (isDesc) albums.sortedByDescending { it.name } else albums.sortedBy { it.name }
                SortOrder.AUTHOR -> if (isDesc) albums.sortedByDescending { it.artist } else albums.sortedBy { it.artist }
                SortOrder.DURATION -> if (isDesc) albums.sortedByDescending { it.durationMs } else albums.sortedBy { it.durationMs}
                SortOrder.LAST_ADDED -> if (isDesc) albums.sortedByDescending { it.year } else albums.sortedBy { it.year }
            }
        }
    }
}