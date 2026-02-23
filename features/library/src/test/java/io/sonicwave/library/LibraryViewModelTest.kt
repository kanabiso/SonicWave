package io.sonicwave.library

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.sonicwave.library.domain.usecase.GetAudioTracksUseCase
import io.sonicwave.library.domain.usecase.PlayTrackUseCase
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
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    // 1. Inicjalizacja reguły dla Coroutines
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // 2. Przygotowanie Mocków (zaślepek)
    private val getAudioTracksUseCase: GetAudioTracksUseCase = mockk()
    // relaxed = true oznacza, że nie musimy definiować co zwraca funkcja, jeśli nic nie zwraca (Unit)
    private val playTrackUseCase: PlayTrackUseCase = mockk(relaxed = true)

    private lateinit var viewModel: LibraryViewModel

    @Test
    fun `should emit loading state initially and then map audio tracks successfully`() = runTest {
        //  ARRANGE
        val mockTrack = AudioTrack(
            id = "1",
            title = "Bohemian Rhapsody",
            artist = "Queen",
            album = "A Night at the Opera",
            uri = "content://media/external/audio/media/1",
            durationMs = 354000L, // 5:54
            albumId = "10",
            track = "1",
            year = "1975",
            dateAdded = "2023-01-01"
        )
        every { getAudioTracksUseCase() } returns flow {
            kotlinx.coroutines.delay(10)
            emit(listOf(mockTrack))
        }

        // ACT
        viewModel = LibraryViewModel(getAudioTracksUseCase, playTrackUseCase)

        //ASSERT
        viewModel.uiState.test {

            val initialState = awaitItem()
            assertTrue("Initial state isLoading = true", initialState.isLoading)
            assertTrue("Initial list should be empty", initialState.audioFiles.isEmpty())

            val successState = awaitItem()
            assertFalse("After loading, isLoading should be false", successState.isLoading)
            assertEquals("List should contain 1 item", 1, successState.audioFiles.size)

            val uiTrack = successState.audioFiles.first()
            assertEquals("1", uiTrack.id)
            assertEquals("Bohemian Rhapsody", uiTrack.title)
            assertEquals("Queen", uiTrack.artist)
            assertEquals("5:54", uiTrack.duration) // Zakładam, że formatAsDuration tak to sformatuje
            assertFalse("Track should not be playing by default", uiTrack.isPlaying)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when OnTrackClick event is triggered then PlayTrackUseCase should be called`() = runTest {
        // ARRANGE
        every { getAudioTracksUseCase() } returns flowOf(emptyList())
        viewModel = LibraryViewModel(getAudioTracksUseCase, playTrackUseCase)

        // ACT
        val testTrackId = "track_123"
        viewModel.onEvent(LibraryUiEvent.OnTrackClick(testTrackId))

        // ASSERT
        verify(exactly = 1) { playTrackUseCase.invoke(testTrackId) }
    }

    @Test
    fun `should emit empty list and hide loading when usecase returns no tracks`() = runTest {
        // ARRANGE
        every { getAudioTracksUseCase() } returns flow {
            kotlinx.coroutines.delay(10)
            emit(emptyList())
        }

        // ACT
        viewModel = LibraryViewModel(getAudioTracksUseCase, playTrackUseCase)

        // ASSERT
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertTrue("Initial state isLoading = true", initialState.isLoading)

            val emptyState = awaitItem()
            assertFalse("After loading, isLoading should be false", emptyState.isLoading)
            assertTrue("List should be empty", emptyState.audioFiles.isEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should react to dynamic data changes from usecase`() = runTest {
        // ARRANGE
        val track1 = AudioTrack("1", "Song 1", "Artist", null, "uri", 1000L, "1", "1", "2023", "date")
        val track2 = AudioTrack("2", "Song 2", "Artist", null, "uri", 1000L, "1", "1", "2023", "date")

        every { getAudioTracksUseCase() } returns flow {
            kotlinx.coroutines.delay(10)
            emit(listOf(track1))
            kotlinx.coroutines.delay(50)
            emit(listOf(track1, track2))
        }

        // ACT
        viewModel = LibraryViewModel(getAudioTracksUseCase, playTrackUseCase)

        // ASSERT
        viewModel.uiState.test {
            assertTrue(awaitItem().isLoading)

            val firstUpdate = awaitItem()
            assertEquals(1, firstUpdate.audioFiles.size)
            assertEquals("Song 1", firstUpdate.audioFiles[0].title)

            val secondUpdate = awaitItem()
            assertEquals(2, secondUpdate.audioFiles.size)
            assertEquals("Song 2", secondUpdate.audioFiles[1].title)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit error state when usecase throws exception`() = runTest {
        // ARRANGE
        val errorMessage = "Something went wrong"
        every { getAudioTracksUseCase() } returns flow {
            kotlinx.coroutines.delay(10)
            throw RuntimeException(errorMessage)
        }

        // ACT
        viewModel = LibraryViewModel(getAudioTracksUseCase, playTrackUseCase)

        // ASSERT
        viewModel.uiState.test {
            assertTrue(awaitItem().isLoading)

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertTrue(errorState.audioFiles.isEmpty())
            assertEquals(errorMessage, errorState.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should correctly map track with null values and short duration`() = runTest {
        // ARRANGE
        val weirdTrack = AudioTrack(
            id = "999",
            title = "Weird Track",
            artist = "Unknown",
            album = null,
            uri = "uri",
            durationMs = 5000L,
            albumId = "0",
            track = "",
            year = "",
            dateAdded = "now"
        )

        every { getAudioTracksUseCase() } returns flow {
            kotlinx.coroutines.delay(10)
            emit(listOf(weirdTrack))
        }

        // ACT
        viewModel = LibraryViewModel(getAudioTracksUseCase, playTrackUseCase)

        // --- ASSERT ---
        viewModel.uiState.test {
            awaitItem()

            val successState = awaitItem()
            val uiTrack = successState.audioFiles.first()

            assertEquals("999", uiTrack.id)
            assertEquals(null, uiTrack.album)
            assertEquals("0:05", uiTrack.duration)

            cancelAndIgnoreRemainingEvents()
        }
    }
}