package io.sonicwave.library.ui

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.sonicwave.common.utils.formatAsDuration
import io.sonicwave.library.domain.usecase.GetAudioTracksUseCase
import io.sonicwave.library.domain.usecase.GetSortedAlbumsUseCase
import io.sonicwave.library.domain.usecase.GetSortedAudioTracksUseCase
import io.sonicwave.library.domain.usecase.PlayTrackUseCase
import io.sonicwave.media.model.AudioAlbum
import io.sonicwave.media.model.AudioTrack
import jakarta.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.io.File

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getAudioTracksUseCase: GetAudioTracksUseCase,
    private val playTrackUseCase: PlayTrackUseCase,
    private val getSortedAudioTracksUseCase: GetSortedAudioTracksUseCase,
    private val getSortedAlbumsUseCase: GetSortedAlbumsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState(isLoading = true))
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    private val sortOrderFlow = _uiState.map { it.sortOrder }.distinctUntilChanged()
    private val isDescFlow = _uiState.map { it.isDesc }.distinctUntilChanged()
    private val isAlbumGroupFlow = _uiState.map { it.isAlbumGroup }.distinctUntilChanged()

    private val albumArtBaseUri = "content://media/external/audio/albumart"

    @OptIn(ExperimentalCoroutinesApi::class)
    val libraryItems: StateFlow<ImmutableList<LibraryItemUiModel>> = isAlbumGroupFlow
        .flatMapLatest { isAlbumGroup ->
            if (isAlbumGroup) {
                getSortedAlbumsUseCase(sortOrderFlow, isDescFlow).map { albums ->
                    albums.map { album ->
                        album.toUiModel()
                    }
                }
            } else {
                getSortedAudioTracksUseCase(sortOrderFlow, isDescFlow).map { tracks ->
                    tracks.map { track ->
                        track.toUiModel(isPlaying = false)
                    }
                }
            }
        }
        .map { items-> items.toImmutableList() }
        .onStart { _uiState.update { it.copy(isLoading = false) } }
        .catch { exception -> _uiState.update { it.copy(isLoading = false, errorMessage = exception.message) } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = persistentListOf()
        )

    fun onEvent(event: LibraryUiEvent) {
        when (event) {
            is LibraryUiEvent.OnTrackClick -> playTrackUseCase(event.trackId)
            is LibraryUiEvent.OnTrackLongClick -> {}
            is LibraryUiEvent.OnFilterClick -> {}
            is LibraryUiEvent.OnAlbumIconClick -> {
                _uiState.update { it.copy(isAlbumGroup = !it.isAlbumGroup) }
            }
            is LibraryUiEvent.OnApplyFilters -> {
                _uiState.update {
                    it.copy(
                        sortOrder = event.sortOrder,
                        isDesc = event.isDesc,
                        isAlbumGroup = event.isAlbumGroup,
                    )
                }
            }
        }
    }

    private fun getCoverUriString(folderPath: String?): String {
        if (folderPath.isNullOrBlank()) return ""

        val directory = File(folderPath)
        if (!directory.exists() || !directory.isDirectory) return ""

        val commonNames = listOf("cover.jpg", "cover.png", "folder.jpg", "folder.png")
        for (name in commonNames) {
            val file = File(directory, name)
            if (file.exists()) {
                return android.net.Uri.fromFile(file).toString()
            }
        }

        val validExtensions = listOf("jpg", "jpeg", "png")
        val imageFile = directory.listFiles()?.firstOrNull { file ->
            file.isFile && file.extension.lowercase() in validExtensions
        }

        if (imageFile != null) {
            return android.net.Uri.fromFile(imageFile).toString()
        }

        return ""
    }

    private fun AudioTrack.toUiModel(isPlaying: Boolean): TrackUiModel {
        val folderPath = File(this.dataPath).parent ?: ""

        return TrackUiModel(
            id = this.id,
            title = this.title,
            artist = this.artist,
            album = this.album,
            albumId = this.albumId,
            duration = this.durationMs.formatAsDuration(),
            isPlaying = isPlaying,
            coverUri = getCoverUriString(folderPath)
        )
    }

    private fun AudioAlbum.toUiModel(): AlbumUiModel {
        return AlbumUiModel(
            id = this.albumId,
            name = this.name,
            artist = this.artist,
            year = this.year,
            duration = this.durationMs?.formatAsDuration() ?: "",
            trackCount = this.trackCount,
            coverUri = getCoverUriString(this.folderPath)
        )
    }
}