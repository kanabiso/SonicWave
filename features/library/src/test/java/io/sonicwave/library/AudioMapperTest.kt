package io.sonicwave.library

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.sonicwave.library.data.mapper.toAudioTrack
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AudioMapperTest {

    @Before
    fun setup() {
        mockkStatic(ContentUris::class)
    }

    @After
    fun teardown() {
        unmockkStatic(ContentUris::class)
    }

    @Test
    fun `toAudioTrack should correctly map full cursor data`() {
        // ARRANGE
        val cursor = mockk<Cursor>()

        val mockUri = mockk<Uri>()

        every { ContentUris.withAppendedId(any(), 101L) } returns mockUri

        every { mockUri.toString() } returns "content://media/external/audio/media/101"

        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID) } returns 0
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE) } returns 1
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST) } returns 2
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM) } returns 3
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION) } returns 4
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID) } returns 5
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK) } returns 6
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR) } returns 7
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED) } returns 8

        every { cursor.getLong(0) } returns 101L
        every { cursor.getString(1) } returns "Test Title"
        every { cursor.getString(2) } returns "Test Artist"
        every { cursor.getString(3) } returns "Test Album"
        every { cursor.getLong(4) } returns 200000L
        every { cursor.getLong(5) } returns 55L
        every { cursor.getString(6) } returns "1"
        every { cursor.getString(7) } returns "2023"
        every { cursor.getString(8) } returns "1672531200"

        // --- ACT ---
        val result = cursor.toAudioTrack()

        // --- ASSERT ---
        assertEquals("101", result.id)
        assertEquals("Test Title", result.title)
        assertEquals("Test Artist", result.artist)
        assertEquals("content://media/external/audio/media/101", result.uri)
    }

    @Test
    fun `toAudioTrack should map null string values to default fallbacks`() {
        val cursor = mockk<Cursor>()
        val mockUri = mockk<Uri>()

        every { ContentUris.withAppendedId(any(), 101L) } returns mockUri

        every { mockUri.toString() } returns "content://media/external/audio/media/101"

        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID) } returns 0
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE) } returns 1
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST) } returns 2
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM) } returns 3
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION) } returns 4
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID) } returns 5
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK) } returns 6
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR) } returns 7
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED) } returns 8

        every { cursor.getLong(0) } returns 101L
        every { cursor.getString(1) } returns null
        every { cursor.getString(2) } returns null
        every { cursor.getString(3) } returns "Test Album"
        every { cursor.getLong(4) } returns 200000L
        every { cursor.getLong(5) } returns 55L
        every { cursor.getString(6) } returns "1"
        every { cursor.getString(7) } returns "2023"
        every { cursor.getString(8) } returns "1672531200"

        val result = cursor.toAudioTrack()

        assertEquals("Unknown Title", result.title)
        assertEquals("Unknown Artist", result.artist)
    }
}