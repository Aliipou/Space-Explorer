package com.example.spaceexplorer

import com.example.spaceexplorer.utils.DateUtils
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class DateUtilsTest {

    @Test
    fun `formatDateForApi returns correct format`() {
        // Given
        val date = LocalDate.of(2024, 1, 15)

        // When
        val result = DateUtils.formatDateForApi(date)

        // Then
        assertEquals("2024-01-15", result)
    }

    @Test
    fun `formatDateForApi handles single digit month and day`() {
        // Given
        val date = LocalDate.of(2024, 3, 5)

        // When
        val result = DateUtils.formatDateForApi(date)

        // Then
        assertEquals("2024-03-05", result)
    }

    @Test
    fun `formatApiDateForDisplay converts to readable format`() {
        // Given
        val apiDate = "2024-01-15"

        // When
        val result = DateUtils.formatApiDateForDisplay(apiDate)

        // Then
        assertTrue(result.contains("2024"))
        assertTrue(result.contains("15"))
        // Month name depends on locale, so just check it's not the original
        assertNotEquals(apiDate, result)
    }

    @Test
    fun `formatApiDateForDisplay handles invalid date gracefully`() {
        // Given
        val invalidDate = "invalid-date"

        // When
        val result = DateUtils.formatApiDateForDisplay(invalidDate)

        // Then
        assertEquals(invalidDate, result)
    }

    @Test
    fun `formatApiDateForDisplay handles partial date gracefully`() {
        // Given
        val partialDate = "2024-01"

        // When
        val result = DateUtils.formatApiDateForDisplay(partialDate)

        // Then
        assertEquals(partialDate, result)
    }

    @Test
    fun `getCurrentDateForApi returns today's date`() {
        // Given
        val today = LocalDate.now()
        val expectedFormat = today.toString()

        // When
        val result = DateUtils.getCurrentDateForApi()

        // Then
        assertEquals(expectedFormat, result)
    }

    @Test
    fun `getPastDateForApi returns correct past date`() {
        // Given
        val daysAgo = 7L
        val expectedDate = LocalDate.now().minusDays(daysAgo).toString()

        // When
        val result = DateUtils.getPastDateForApi(daysAgo)

        // Then
        assertEquals(expectedDate, result)
    }

    @Test
    fun `getPastDateForApi with zero days returns today`() {
        // Given
        val daysAgo = 0L

        // When
        val result = DateUtils.getPastDateForApi(daysAgo)

        // Then
        assertEquals(DateUtils.getCurrentDateForApi(), result)
    }

    @Test
    fun `formatApiDateForDisplay handles leap year date`() {
        // Given
        val leapYearDate = "2024-02-29"

        // When
        val result = DateUtils.formatApiDateForDisplay(leapYearDate)

        // Then
        assertTrue(result.contains("2024"))
        assertTrue(result.contains("29"))
    }
}
