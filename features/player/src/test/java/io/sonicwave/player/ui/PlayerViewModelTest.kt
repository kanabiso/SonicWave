package io.sonicwave.player.ui

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import io.sonicwave.player.domain.GetPlayerStateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getPlayerStateUseCase: GetPlayerStateUseCase
    private lateinit var viewModel: PlayerViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getPlayerStateUseCase = mockk()
        every { getPlayerStateUseCase() } returns flowOf(PlayerUiState())
        viewModel = PlayerViewModel(getPlayerStateUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given PlayPause event When track is not playing Then track should start playing`() = runTest {
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(false, initialState.isPlaying)

            viewModel.onEvent(PlayerUiEvent.OnPlayPauseClick)

            val updatedState = awaitItem()
            assertEquals(true, updatedState.isPlaying)
        }
    }

    @Test
    fun `Given FavoriteToggle event When track is not favorite Then track should become favorite`() = runTest {
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(false, initialState.isFavorite)

            viewModel.onEvent(PlayerUiEvent.OnFavoriteToggle)

            val updatedState = awaitItem()
            assertEquals(true, updatedState.isFavorite)
        }
    }
}