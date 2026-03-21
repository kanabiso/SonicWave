package io.sonicwave.library.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    val isAlbumListLayout: Flow<Boolean>
    suspend fun setAlbumListLayout(isList: Boolean)
    val isTracksListLayout: Flow<Boolean>
    suspend fun setTracksListLayout(isList: Boolean)
}
