package com.example.spaceexplorer.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/**
 * Utility class for date-related operations
 */
object DateUtils {
    private val apiDateFormat = DateTimeFormatter.ofPattern(Constants.API_DATE_FORMAT)
    private val displayDateFormat = SimpleDateFormat(Constants.DISPLAY_DATE_FORMAT, Locale.getDefault())
    
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
        val parts = apiDateString.split("-")
        if (parts.size != 3) return apiDateString
        
        try {
            val year = parts[0].toInt()
            val month = parts[1].toInt() - 1 // 0-based month
            val day = parts[2].toInt()
            
            val date = Date(year - 1900, month, day)
            return displayDateFormat.format(date)
        } catch (e: Exception) {
            return apiDateString
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