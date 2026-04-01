package io.sonicwave.library.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.sonicwave.common.utils.formatAsDuration
import io.sonicwave.library.domain.usecase.PlayTrackUseCase
import io.sonicwave.library.domain.usecase.GetAlbumLayoutPreferenceUseCase
import io.sonicwave.library.domain.usecase.GetAudioTracksUseCase
import io.sonicwave.library.domain.usecase.GetSortedAlbumsUseCase
import io.sonicwave.library.domain.usecase.GetSortedAudioTracksUseCase
import io.sonicwave.library.domain.usecase.GetTracksLayoutPreferenceUseCase
import io.sonicwave.library.domain.usecase.SetAlbumLayoutPreferenceUseCase
import io.sonicwave.library.domain.usecase.SetTracksLayoutPreferenceUseCase
import io.sonicwave.media.domain.model.AudioAlbum
import io.sonicwave.media.domain.model.AudioTrack
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getAudioTracksUseCase: GetAudioTracksUseCase,
    private val playTrackUseCase: PlayTrackUseCase,
    private val getSortedAudioTracksUseCase: GetSortedAudioTracksUseCase,
    private val getSortedAlbumsUseCase: GetSortedAlbumsUseCase,
    private val getAlbumLayoutPreferenceUseCase: GetAlbumLayoutPreferenceUseCase,
    private val setAlbumLayoutPreferenceUseCase: SetAlbumLayoutPreferenceUseCase,
    private val getTracksLayoutPreferenceUseCase: GetTracksLayoutPreferenceUseCase,
    private val setTracksLayoutPreferenceUseCase: SetTracksLayoutPreferenceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState(isLoading = true))
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        getAlbumLayoutPreferenceUseCase()
            .onEach { isList ->
                _uiState.update { it.copy(isAlbumListLayout = isList) }
            }
            .launchIn(viewModelScope)

        getTracksLayoutPreferenceUseCase()
            .onEach { isList ->
                _uiState.update { it.copy(isTracksListLayout = isList) }
            }
            .launchIn(viewModelScope)
    }

    private val sortOrderFlow = _uiState.map { it.sortOrder }.distinctUntilChanged()
    private val isDescFlow = _uiState.map { it.isDesc }.distinctUntilChanged()
    private val isAlbumGroupFlow = _uiState.map { it.isAlbumGroup }.distinctUntilChanged()

    private val refreshTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val libraryItems: StateFlow<ImmutableList<LibraryItemUiModel>> = combine(
        isAlbumGroupFlow,
        refreshTrigger
    ) { isAlbumGroup, _ -> isAlbumGroup }
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
        .onEach { _uiState.update { it.copy(isLoading = false) } }
        .catch { exception -> _uiState.update { it.copy(isLoading = false, errorMessage = exception.message) } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = persistentListOf()
        )

    fun onEvent(event: LibraryUiEvent) {
        when (event) {
            is LibraryUiEvent.OnTrackClick -> {
                viewModelScope.launch {
                    playTrackUseCase(event.trackId)

                }
            }
            is LibraryUiEvent.OnTrackLongClick -> {}
            is LibraryUiEvent.OnFilterClick -> {}
            is LibraryUiEvent.OnAlbumIconClick -> {
                _uiState.update { it.copy(isAlbumGroup = !it.isAlbumGroup) }
            }
            is LibraryUiEvent.OnAlbumLayoutToggleClick -> {
                viewModelScope.launch {
                    setAlbumLayoutPreferenceUseCase(!_uiState.value.isAlbumListLayout)
                }
            }
            is LibraryUiEvent.OnTracksLayoutToggleClick -> {
                viewModelScope.launch {
                    setTracksLayoutPreferenceUseCase(!_uiState.value.isTracksListLayout)
                }
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
            is LibraryUiEvent.OnPermissionsGranted -> {
                _uiState.update { it.copy(isLoading = true) }
                refreshTrigger.value += 1
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