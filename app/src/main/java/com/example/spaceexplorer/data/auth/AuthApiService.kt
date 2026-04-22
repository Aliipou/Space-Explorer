package com.example.spaceexplorer.data.auth

import retrofit2.http.*

interface AuthApiService {
    @POST("v1/auth/register")
    suspend fun register(@Body body: RegisterRequest): TokenResponse

    @POST("v1/auth/login")
    suspend fun login(@Body body: LoginRequest): TokenResponse

    @POST("v1/auth/refresh")
    suspend fun refresh(@Body body: RefreshRequest): TokenResponse

    @POST("v1/auth/logout")
    suspend fun logout(@Header("Authorization") bearer: String)

    @GET("v1/auth/me")
    suspend fun me(@Header("Authorization") bearer: String): UserProfile

    @GET("v1/profile")
    suspend fun getProfile(@Header("Authorization") bearer: String): UserProfile

    @PATCH("v1/profile")
    suspend fun updateProfile(
        @Header("Authorization") bearer: String,
        @Body body: ProfileUpdate
    ): UserProfile

    @POST("v1/favorites/sync")
    suspend fun syncFavorites(
        @Header("Authorization") bearer: String,
        @Body body: FavoriteSyncRequest
    ): SyncResponse
}
