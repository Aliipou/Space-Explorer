package com.example.spaceexplorer.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Utility class for date-related operations
 */
object DateUtils {
    private val apiDateFormat = DateTimeFormatter.ofPattern(Constants.API_DATE_FORMAT)
    private val displayDateFormat = DateTimeFormatter.ofPattern(
        Constants.DISPLAY_DATE_FORMAT,
        Locale.getDefault()
    )

    /**
     * Format a date string for API usage
     * @param date The date to format
     * @return Formatted date string (YYYY-MM-DD)
     */
    fun formatDateForApi(date: LocalDate): String {
        return date.format(apiDateFormat)
    }

    /**
     * Format a date string from API to a user-friendly format
     * @param apiDateString The API date string (YYYY-MM-DD)
     * @return User-friendly date string (Month Day, Year)
     */
    fun formatApiDateForDisplay(apiDateString: String): String {
        return try {
            val localDate = LocalDate.parse(apiDateString, apiDateFormat)
            localDate.format(displayDateFormat)
        } catch (e: Exception) {
            apiDateString
        }
    }

    /**
     * Get the current date formatted for API usage
     * @return Current date string (YYYY-MM-DD)
     */
    fun getCurrentDateForApi(): String {
        return formatDateForApi(LocalDate.now())
    }

    /**
     * Get a date from [daysAgo] days in the past formatted for API
     * @param daysAgo Number of days in the past
     * @return Past date string (YYYY-MM-DD)
     */
    fun getPastDateForApi(daysAgo: Long): String {
        return formatDateForApi(LocalDate.now().minusDays(daysAgo))
    }
}