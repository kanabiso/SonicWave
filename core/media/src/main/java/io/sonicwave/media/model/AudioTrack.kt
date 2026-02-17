package io.sonicwave.media.model

data class AudioTrack(
    val id: String,
    val title: String,
    val artist: String,
    val uri: String,
    val durationMs: Long
)