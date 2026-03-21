package io.sonicwave.library.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import io.sonicwave.library.domain.repository.PreferenceRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStorePreferenceRepository @Inject constructor(
    private val context: Context
) : PreferenceRepository {

    private object PreferencesKeys {
        val IS_ALBUM_LIST_LAYOUT = booleanPreferencesKey("is_album_list_layout")
        val IS_TRACK_LIST_LAYOUT = booleanPreferencesKey("is_tracks_list_layout")
    }

    override val isAlbumListLayout: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.IS_ALBUM_LIST_LAYOUT] ?: true
        }

    override suspend fun setAlbumListLayout(isList: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_ALBUM_LIST_LAYOUT] = isList
        }
    }

    override val isTracksListLayout: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.IS_TRACK_LIST_LAYOUT] ?: true
        }

    override suspend fun setTracksListLayout(isList: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_TRACK_LIST_LAYOUT] = isList
        }
    }
}
