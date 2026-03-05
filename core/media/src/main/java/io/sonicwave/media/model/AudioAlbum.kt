package io.sonicwave.media.model

data class AudioAlbum(
    val albumId: Long,
    val year: String?,
    val artist: String?,
    val name: String?,
    val durationMs: Long?,
    val trackCount: Int?,
    val firstTrackUri: String,
    val folderPath: String
)