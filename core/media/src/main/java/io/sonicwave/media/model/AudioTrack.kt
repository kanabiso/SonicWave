package io.sonicwave.media.model

data class AudioTrack(
    val id: String,
    val title: String,
    val artist: String,
    val album: String? = null,
    val uri: String,
    val durationMs: Long,
    val albumId: String,
    val track: String,
    val year: String,
    val dateAdded: String
)