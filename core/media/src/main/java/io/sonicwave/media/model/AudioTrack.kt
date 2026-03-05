package io.sonicwave.media.model

data class AudioTrack(
    val id: Long,
    val title: String?,
    val artist: String?,
    val album: String?,
    val uri: String,
    val durationMs: Long,
    val albumId: Long,
    val track: String?,
    val year: String?,
    val dateAdded: String?,
    val dataPath: String
)