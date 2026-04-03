package io.sonicwave.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route: NavKey {
    @Serializable
    data object Library : Route, NavKey

    @Serializable
    data class Album(val albumId: Long) : Route, NavKey

}