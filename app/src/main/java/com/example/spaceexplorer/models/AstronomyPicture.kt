package com.example.spaceexplorer.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Data class representing the Astronomy Picture of the Day (APOD) from NASA API
 */
data class AstronomyPicture(
    // Date when the picture was featured
    val date: String,
    
    // Detailed explanation of the astronomy picture
    val explanation: String,
    
    // URL for the high definition image
    @SerializedName("hdurl")
    val hdUrl: String?,
    
    // Media type (image or video)
    @SerializedName("media_type")
    val mediaType: String,
    
    // API service version
    @SerializedName("service_version")
    val serviceVersion: String,
    
    // Title of the astronomy picture
    val title: String,
    
    // URL for the standard definition image or video
    val url: String,
    
    // Copyright information (may be null for public domain images)
    val copyright: String?
) : Serializable {
    
    /**
     * Check if this media item is an image or video
     * @return True if media type is image, false otherwise
     */
    fun isImage(): Boolean = mediaType == "image"
    
    /**
     * Get formatted copyright text with year
     * @return Formatted copyright string or empty if null
     */
    fun getFormattedCopyright(): String {
        return if (copyright != null) "© $copyright" else ""
    }
    
    /**
     * Get a shortened explanation suitable for list view
     * @param maxLength Maximum length of the summary
     * @return Shortened explanation with ellipsis if needed
     */
    fun getShortExplanation(maxLength: Int = 100): String {
        return if (explanation.length > maxLength) {
            explanation.substring(0, maxLength) + "..."
        } else {
            explanation
        }
    }
}