package io.sonicwave.player.ui.model

import androidx.annotation.StringRes
import io.sonicwave.player.R

enum class PlayerOptions(@param:StringRes val label: Int) {
    Go_to_Album(R.string.go_to_album),
    Go_to_Artist(R.string.go_to_artist),
    Show_Lyrics(R.string.show_lyrics),
    Add_to_Playlist(R.string.add_to_playlist)
}