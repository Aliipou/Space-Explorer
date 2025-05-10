package com.example.spaceexplorer.api

import com.example.spaceexplorer.BuildConfig
import com.example.spaceexplorer.models.AstronomyPicture
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface defining the NASA API endpoints
 */
interface NasaApiService {
    /**
     * Get the Astronomy Picture of the Day (APOD)
     * @param apiKey NASA API key for access
     * @param count Number of random items to retrieve
     * @return List of AstronomyPicture objects
     */
    @GET("planetary/apod")
    suspend fun getAstronomyPictures(
        @Query("api_key") apiKey: String = BuildConfig.NASA_API_KEY,
        @Query("count") count: Int = 20
    ): List<AstronomyPicture>
    
    /**
     * Get the Astronomy Picture of the Day for a specific date range
     * @param apiKey NASA API key for access
     * @param startDate Beginning date (YYYY-MM-DD)
     * @param endDate Ending date (YYYY-MM-DD)
     * @return List of AstronomyPicture objects
     */
    @GET("planetary/apod")
    suspend fun getAstronomyPicturesByDateRange(
        @Query("api_key") apiKey: String = BuildConfig.NASA_API_KEY,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): List<AstronomyPicture>
    
    companion object {
        const val BASE_URL = "https://api.nasa.gov/"
    }
}