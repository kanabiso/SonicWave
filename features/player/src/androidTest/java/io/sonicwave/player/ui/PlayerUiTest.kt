package io.sonicwave.player.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class PlayerUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun playerScreen_displaysTrackInfo() {
        val state = PlayerUiState(
            track = MockTrack(
                title = "Test Track",
                artist = "Test Artist",
                album = "Test Album",
                coverArtUrl = null,
                durationMs = 1000L
            )
        )

        composeTestRule.setContent {
            PlayerScreen(state = state, onEvent = {})
        }

        composeTestRule.onNodeWithText("Test Track").assertExists()
        composeTestRule.onNodeWithText("Test Artist • Test Album").assertExists()
    }

    @Test
    fun playerScreen_playPauseClick_triggersEvent() {
        var clicked = false
        val state = PlayerUiState()

        composeTestRule.setContent {
            PlayerScreen(state = state, onEvent = {
                if (it is PlayerUiEvent.OnPlayPauseClick) clicked = true
            })
        }

        composeTestRule.onNodeWithContentDescription("Play").performClick()
        assert(clicked)
    }
}