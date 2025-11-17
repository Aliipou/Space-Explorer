package com.example.spaceexplorer

import com.example.spaceexplorer.models.AstronomyPicture
import org.junit.Assert.*
import org.junit.Test

class AstronomyPictureTest {

    private val sampleImagePicture = AstronomyPicture(
        date = "2024-01-15",
        explanation = "A beautiful spiral galaxy captured by the Hubble Space Telescope. " +
                "This galaxy shows intricate spiral arms with young blue stars and older red stars.",
        hdUrl = "https://apod.nasa.gov/apod/image/2401/galaxy_hd.jpg",
        mediaType = "image",
        serviceVersion = "v1",
        title = "Spiral Galaxy NGC 1234",
        url = "https://apod.nasa.gov/apod/image/2401/galaxy.jpg",
        copyright = "NASA/ESA"
    )

    private val sampleVideoPicture = AstronomyPicture(
        date = "2024-01-14",
        explanation = "A video showing the rotation of Earth from space.",
        hdUrl = null,
        mediaType = "video",
        serviceVersion = "v1",
        title = "Earth Rotation Timelapse",
        url = "https://www.youtube.com/embed/xyz123",
        copyright = null
    )

    @Test
    fun `isImage returns true for image media type`() {
        // When
        val result = sampleImagePicture.isImage()

        // Then
        assertTrue(result)
    }

    @Test
    fun `isImage returns false for video media type`() {
        // When
        val result = sampleVideoPicture.isImage()

        // Then
        assertFalse(result)
    }

    @Test
    fun `getFormattedCopyright returns formatted string when copyright exists`() {
        // When
        val result = sampleImagePicture.getFormattedCopyright()

        // Then
        assertEquals("© NASA/ESA", result)
    }

    @Test
    fun `getFormattedCopyright returns empty string when copyright is null`() {
        // When
        val result = sampleVideoPicture.getFormattedCopyright()

        // Then
        assertEquals("", result)
    }

    @Test
    fun `getShortExplanation truncates long text`() {
        // When
        val result = sampleImagePicture.getShortExplanation(50)

        // Then
        assertTrue(result.length <= 53) // max 50 + "..."
        assertTrue(result.endsWith("..."))
        assertTrue(result.startsWith("A beautiful spiral galaxy"))
    }

    @Test
    fun `getShortExplanation does not truncate short text`() {
        // Given
        val shortExplanation = "Short text"
        val picture = sampleImagePicture.copy(explanation = shortExplanation)

        // When
        val result = picture.getShortExplanation(100)

        // Then
        assertEquals(shortExplanation, result)
        assertFalse(result.endsWith("..."))
    }

    @Test
    fun `getShortExplanation uses default length of 100`() {
        // When
        val result = sampleImagePicture.getShortExplanation()

        // Then
        assertEquals(103, result.length) // 100 + "..."
    }

    @Test
    fun `getShortExplanation handles exact length match`() {
        // Given
        val exactText = "A".repeat(100)
        val picture = sampleImagePicture.copy(explanation = exactText)

        // When
        val result = picture.getShortExplanation(100)

        // Then
        assertEquals(exactText, result)
        assertFalse(result.endsWith("..."))
    }

    @Test
    fun `getShortExplanation handles text just over max length`() {
        // Given
        val overText = "A".repeat(101)
        val picture = sampleImagePicture.copy(explanation = overText)

        // When
        val result = picture.getShortExplanation(100)

        // Then
        assertEquals(103, result.length) // 100 + "..."
        assertTrue(result.endsWith("..."))
    }

    @Test
    fun `data class equality works correctly`() {
        // Given
        val picture1 = sampleImagePicture
        val picture2 = sampleImagePicture.copy()

        // Then
        assertEquals(picture1, picture2)
    }

    @Test
    fun `data class inequality with different title`() {
        // Given
        val picture1 = sampleImagePicture
        val picture2 = sampleImagePicture.copy(title = "Different Title")

        // Then
        assertNotEquals(picture1, picture2)
    }

    @Test
    fun `hdUrl can be null for some pictures`() {
        // Then
        assertNotNull(sampleImagePicture.hdUrl)
        assertNull(sampleVideoPicture.hdUrl)
    }

    @Test
    fun `copyright can be null for public domain images`() {
        // Then
        assertNotNull(sampleImagePicture.copyright)
        assertNull(sampleVideoPicture.copyright)
    }
}
