package com.example.spaceexplorer

import com.example.spaceexplorer.models.AstronomyPicture
import org.junit.Assert.*
import org.junit.Test

class AstronomyPictureModelTest {

    private fun fixture(
        date: String = "2024-01-15",
        title: String = "Andromeda Galaxy",
        mediaType: String = "image",
        url: String = "https://apod.nasa.gov/apod/image/test.jpg",
        copyright: String? = "NASA"
    ) = AstronomyPicture(
        date = date,
        explanation = "A stunning view of the Andromeda galaxy.",
        hdUrl = url,
        mediaType = mediaType,
        serviceVersion = "v1",
        title = title,
        url = url,
        copyright = copyright
    )

    @Test
    fun `isImage returns true when mediaType is image`() {
        assertTrue(fixture(mediaType = "image").isImage())
    }

    @Test
    fun `isImage returns false when mediaType is video`() {
        assertFalse(fixture(mediaType = "video").isImage())
    }

    @Test
    fun `getFormattedCopyright prepends copyright symbol`() {
        val pic = fixture(copyright = "NASA/ESA")
        assertEquals("© NASA/ESA", pic.getFormattedCopyright())
    }

    @Test
    fun `getFormattedCopyright returns empty when null`() {
        val pic = fixture(copyright = null)
        assertEquals("", pic.getFormattedCopyright())
    }

    @Test
    fun `getShortExplanation truncates at maxLength`() {
        val long = "A".repeat(200)
        val pic = fixture().copy(explanation = long)
        val result = pic.getShortExplanation(maxLength = 100)
        assertEquals(103, result.length) // 100 + "..."
        assertTrue(result.endsWith("..."))
    }

    @Test
    fun `getShortExplanation does not truncate short text`() {
        val short = "Short."
        val pic = fixture().copy(explanation = short)
        assertEquals(short, pic.getShortExplanation(maxLength = 100))
    }

    @Test
    fun `getShortExplanation uses default maxLength of 100`() {
        val exact = "B".repeat(100)
        val pic = fixture().copy(explanation = exact)
        assertEquals(exact, pic.getShortExplanation())
    }

    @Test
    fun `data class equality works correctly`() {
        val p1 = fixture()
        val p2 = fixture()
        assertEquals(p1, p2)
    }

    @Test
    fun `data class copy creates independent instance`() {
        val original = fixture(title = "Original")
        val copy = original.copy(title = "Copy")
        assertNotEquals(original.title, copy.title)
    }
}
