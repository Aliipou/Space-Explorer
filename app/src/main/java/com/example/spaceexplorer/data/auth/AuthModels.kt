package com.example.spaceexplorer.data.auth

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val email: String,
    val password: String,
    @SerializedName("display_name") val displayName: String? = null
)

data class LoginRequest(val email: String, val password: String)

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int
)

data class RefreshRequest(@SerializedName("refresh_token") val refreshToken: String)

data class UserProfile(
    val id: Int,
    val email: String,
    @SerializedName("display_name") val displayName: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    val bio: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("favorites_count") val favoritesCount: Int = 0,
    @SerializedName("is_active") val isActive: Boolean = true
)

data class ProfileUpdate(
    @SerializedName("display_name") val displayName: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    val bio: String? = null
)

data class FavoriteSyncRequest(val favorites: List<FavoriteSyncItem>)

data class FavoriteSyncItem(
    val date: String,
    val title: String,
    val url: String,
    @SerializedName("hd_url") val hdUrl: String? = null,
    val explanation: String,
    @SerializedName("media_type") val mediaType: String,
    val copyright: String? = null
)

data class SyncResponse(val added: Int, val skipped: Int, val total: Int)
