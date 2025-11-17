package com.example.spaceexplorer

import com.example.spaceexplorer.models.AstronomyPicture
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class ApiResponseParsingTest {

    private val gson = Gson()

    @Test
    fun `parse single APOD response with all fields`() {
        val json = """
        {
            "date": "2024-01-15",
            "explanation": "This is a test explanation for the astronomy picture.",
            "hdurl": "https://apod.nasa.gov/apod/image/2401/test_hd.jpg",
            "media_type": "image",
            "service_version": "v1",
            "title": "Test Astronomy Picture",
            "url": "https://apod.nasa.gov/apod/image/2401/test.jpg",
            "copyright": "Test Copyright"
        }
        """.trimIndent()

        val picture = gson.fromJson(json, AstronomyPicture::class.java)

        assertEquals("2024-01-15", picture.date)
        assertEquals("This is a test explanation for the astronomy picture.", picture.explanation)
        assertEquals("https://apod.nasa.gov/apod/image/2401/test_hd.jpg", picture.hdUrl)
        assertEquals("image", picture.mediaType)
        assertEquals("v1", picture.serviceVersion)
        assertEquals("Test Astronomy Picture", picture.title)
        assertEquals("https://apod.nasa.gov/apod/image/2401/test.jpg", picture.url)
        assertEquals("Test Copyright", picture.copyright)
    }

    @Test
    fun `parse APOD response without optional fields`() {
        val json = """
        {
            "date": "2024-01-14",
            "explanation": "Video explanation",
            "media_type": "video",
            "service_version": "v1",
            "title": "Video Title",
            "url": "https://www.youtube.com/embed/xyz123"
        }
        """.trimIndent()

        val picture = gson.fromJson(json, AstronomyPicture::class.java)

        assertEquals("2024-01-14", picture.date)
        assertNull(picture.hdUrl)
        assertEquals("video", picture.mediaType)
        assertNull(picture.copyright)
    }

    @Test
    fun `parse array of APOD responses`() {
        val json = """
        [
            {
                "date": "2024-01-15",
                "explanation": "First picture",
                "hdurl": "https://example.com/hd1.jpg",
                "media_type": "image",
                "service_version": "v1",
                "title": "First",
                "url": "https://example.com/1.jpg",
                "copyright": "Copyright 1"
            },
            {
                "date": "2024-01-14",
                "explanation": "Second picture",
                "media_type": "image",
                "service_version": "v1",
                "title": "Second",
                "url": "https://example.com/2.jpg"
            }
        ]
        """.trimIndent()

        val pictures = gson.fromJson(json, Array<AstronomyPicture>::class.java)

        assertEquals(2, pictures.size)
        assertEquals("First", pictures[0].title)
        assertEquals("Second", pictures[1].title)
        assertNotNull(pictures[0].copyright)
        assertNull(pictures[1].copyright)
    }

    @Test
    fun `serialize AstronomyPicture to JSON`() {
        val picture = AstronomyPicture(
            date = "2024-01-15",
            explanation = "Test",
            hdUrl = "https://example.com/hd.jpg",
            mediaType = "image",
            serviceVersion = "v1",
            title = "Test",
            url = "https://example.com/img.jpg",
            copyright = "Test"
        )

        val json = gson.toJson(picture)

        assertTrue(json.contains("\"date\":\"2024-01-15\""))
        assertTrue(json.contains("\"hdurl\":\"https://example.com/hd.jpg\""))
        assertTrue(json.contains("\"media_type\":\"image\""))
        assertTrue(json.contains("\"service_version\":\"v1\""))
    }

    @Test
    fun `parse response with special characters in explanation`() {
        val json = """
        {
            "date": "2024-01-15",
            "explanation": "This has \"quotes\" and special chars: & < > ' \n newlines",
            "media_type": "image",
            "service_version": "v1",
            "title": "Special Characters Test",
            "url": "https://example.com/img.jpg"
        }
        """.trimIndent()

        val picture = gson.fromJson(json, AstronomyPicture::class.java)

        assertTrue(picture.explanation.contains("\"quotes\""))
        assertTrue(picture.explanation.contains("&"))
        assertTrue(picture.explanation.contains("<"))
    }

    @Test
    fun `parse response with very long explanation`() {
        val longText = "A".repeat(10000)
        val json = """
        {
            "date": "2024-01-15",
            "explanation": "$longText",
            "media_type": "image",
            "service_version": "v1",
            "title": "Long Text",
            "url": "https://example.com/img.jpg"
        }
        """.trimIndent()

        val picture = gson.fromJson(json, AstronomyPicture::class.java)

        assertEquals(10000, picture.explanation.length)
    }

    @Test
    fun `isImage returns correct value for different media types`() {
        val imageJson = """{"date":"2024-01-15","explanation":"test","media_type":"image","service_version":"v1","title":"Test","url":"url"}"""
        val videoJson = """{"date":"2024-01-15","explanation":"test","media_type":"video","service_version":"v1","title":"Test","url":"url"}"""

        val imagePicture = gson.fromJson(imageJson, AstronomyPicture::class.java)
        val videoPicture = gson.fromJson(videoJson, AstronomyPicture::class.java)

        assertTrue(imagePicture.isImage())
        assertFalse(videoPicture.isImage())
    }
}
