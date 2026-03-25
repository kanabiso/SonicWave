package io.sonicwave.player.ui

data class MockTrack(
    val title: String,
    val artist: String,
    val album: String,
    val coverArtUrl: String?,
    val durationMs: Long
)

data class PlayerUiState(
    val track: MockTrack = MockTrack(
        title = "Mock Track",
        artist = "Mock Artist",
        album = "Mock Album",
        coverArtUrl = null,
        durationMs = 180000L
    ),
    val isPlaying: Boolean = false,
    val isFavorite: Boolean = false,
    val isLooping: Boolean = false,
    val currentPositionMs: Long = 0L,
    val isMinimized: Boolean = false
)