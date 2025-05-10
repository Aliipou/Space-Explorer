package com.example.spaceexplorer.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton class for creating and managing Retrofit instances
 */
object RetrofitClient {
    
    /**
     * Creates a Retrofit client for the NASA API
     * @return Instance of NasaApiService
     */
    fun createNasaApiService(): NasaApiService {
        // Create logging interceptor for debugging
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        // Configure OkHttpClient with timeouts and logging
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        
        // Build Retrofit instance with NASA API base URL
        return Retrofit.Builder()
            .baseUrl(NasaApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NasaApiService::class.java)
    }
}