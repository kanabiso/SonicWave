package io.sonicwave.library.ui

import androidx.lifecycle.ViewModel
import io.sonicwave.library.domain.repository.AudioRepository

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
    private val musicPlayer: MusicPlayer
) : ViewModel() {
    // Logika zarządzania stanem, wykorzystująca StateFlow
}