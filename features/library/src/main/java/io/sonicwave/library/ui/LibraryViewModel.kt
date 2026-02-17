package io.sonicwave.library.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.sonicwave.library.domain.repository.AudioRepository
import io.sonicwave.media.domain.interfaces.MusicPlayer
import jakarta.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
    private val musicPlayer: MusicPlayer
) : ViewModel() {
    // Logika zarządzania stanem, wykorzystująca StateFlow
}