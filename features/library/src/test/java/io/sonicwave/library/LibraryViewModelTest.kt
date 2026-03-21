package io.sonicwave.library

import app.cash.turbine.test
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.sonicwave.library.domain.usecase.GetAudioTracksUseCase
import io.sonicwave.library.domain.usecase.GetAlbumLayoutPreferenceUseCase
import io.sonicwave.library.domain.usecase.GetSortedAlbumsUseCase
import io.sonicwave.library.domain.usecase.GetSortedAudioTracksUseCase
import io.sonicwave.library.domain.usecase.PlayTrackUseCase
import io.sonicwave.library.domain.usecase.SetAlbumLayoutPreferenceUseCase
import io.sonicwave.library.ui.LibraryUiEvent
import io.sonicwave.library.ui.LibraryViewModel
import io.sonicwave.library.utils.MainDispatcherRule
import io.sonicwave.media.model.AudioTrack
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getAudioTracksUseCase: GetAudioTracksUseCase = mockk()
    private val playTrackUseCase: PlayTrackUseCase = mockk(relaxed = true)
    private val getSortedAudioTracksUseCase: GetSortedAudioTracksUseCase = mockk()
    private val getSortedAlbumsUseCase: GetSortedAlbumsUseCase = mockk()
    private val getAlbumLayoutPreferenceUseCase: GetAlbumLayoutPreferenceUseCase = mockk()
    private val setAlbumLayoutPreferenceUseCase: SetAlbumLayoutPreferenceUseCase = mockk(relaxed = true)

    private lateinit var viewModel: LibraryViewModel

    @Before
    fun setup() {
        every { getAlbumLayoutPreferenceUseCase() } returns flowOf(true)
        every { getSortedAudioTracksUseCase(any(), any()) } returns flowOf(emptyList())
        every { getSortedAlbumsUseCase(any(), any()) } returns flowOf(emptyList())
    }

    @Test
    fun `should emit loading state initially and then map audio tracks successfully`() = runTest {
        val mockTrack = AudioTrack(
            id = 1,
            title = "Bohemian Rhapsody",
            artist = "Queen",
            album = "A Night at the Opera",
            uri = "content://media/external/audio/media/1",
            durationMs = 354000L,
            albumId = 10,
            track = "1",
            year = "1975",
            dateAdded = "2023-01-01",
            dataPath = "/path/to/file"
        )
        every { getSortedAudioTracksUseCase(any(), any()) } returns flow {
            kotlinx.coroutines.delay(10)
            emit(listOf(mockTrack))
        }

        viewModel = LibraryViewModel(
            getAudioTracksUseCase,
            playTrackUseCase,
            getSortedAudioTracksUseCase,
            getSortedAlbumsUseCase,
            getAlbumLayoutPreferenceUseCase,
            setAlbumLayoutPreferenceUseCase
        )

        viewModel.libraryItems.test {
            assertTrue(awaitItem().isEmpty()) // initialValue
            val successItems = awaitItem()
            assertEquals(1, successItems.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when OnTrackClick event is triggered then PlayTrackUseCase should be called`() = runTest {
        viewModel = LibraryViewModel(
            getAudioTracksUseCase,
            playTrackUseCase,
            getSortedAudioTracksUseCase,
            getSortedAlbumsUseCase,
            getAlbumLayoutPreferenceUseCase,
            setAlbumLayoutPreferenceUseCase
        )

        val testTrackId = 123L
        viewModel.onEvent(LibraryUiEvent.OnTrackClick(testTrackId))

        verify(exactly = 1) { playTrackUseCase.invoke(testTrackId) }
    }

    @Test
    fun `should load initial layout preference from usecase`() = runTest {
        every { getAlbumLayoutPreferenceUseCase() } returns flowOf(false)

        viewModel = LibraryViewModel(
            getAudioTracksUseCase,
            playTrackUseCase,
            getSortedAudioTracksUseCase,
            getSortedAlbumsUseCase,
            getAlbumLayoutPreferenceUseCase,
            setAlbumLayoutPreferenceUseCase
        )

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse("Initial isListLayout should be false", state.isListLayout)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when OnLayoutToggleClick is triggered then SetLayoutPreferenceUseCase should be called`() = runTest {
        every { getAlbumLayoutPreferenceUseCase() } returns flowOf(true)

        viewModel = LibraryViewModel(
            getAudioTracksUseCase,
            playTrackUseCase,
            getSortedAudioTracksUseCase,
            getSortedAlbumsUseCase,
            getAlbumLayoutPreferenceUseCase,
            setAlbumLayoutPreferenceUseCase
        )

        viewModel.onEvent(LibraryUiEvent.OnLayoutToggleClick)

        coVerify(exactly = 1) { setAlbumLayoutPreferenceUseCase.invoke(false) }
    }
}
