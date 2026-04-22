package com.example.spaceexplorer.data.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(
    private val api: AuthApiService,
    private val store: TokenStore
) {
    private val _user = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _user.asStateFlow()

    val isAuthenticated: Boolean get() = store.accessToken != null

    suspend fun register(email: String, password: String, displayName: String? = null): Result<Unit> =
        runCatching {
            val tokens = api.register(RegisterRequest(email, password, displayName))
            store.accessToken = tokens.accessToken
            store.refreshToken = tokens.refreshToken
            _user.value = api.me("Bearer ${tokens.accessToken}")
        }

    suspend fun login(email: String, password: String): Result<Unit> =
        runCatching {
            val tokens = api.login(LoginRequest(email, password))
            store.accessToken = tokens.accessToken
            store.refreshToken = tokens.refreshToken
            _user.value = api.me("Bearer ${tokens.accessToken}")
        }

    suspend fun logout(): Unit = runCatching {
        store.accessToken?.let { api.logout("Bearer $it") }
    }.also { store.clear(); _user.value = null }.getOrDefault(Unit)

    suspend fun refreshIfNeeded(): Boolean {
        val rt = store.refreshToken ?: return false
        return runCatching {
            val tokens = api.refresh(RefreshRequest(rt))
            store.accessToken = tokens.accessToken
            store.refreshToken = tokens.refreshToken
            true
        }.getOrDefault(false.also { store.clear(); _user.value = null })
    }

    suspend fun restoreSession() {
        val token = store.accessToken ?: return
        runCatching { _user.value = api.me("Bearer $token") }
            .onFailure { if (refreshIfNeeded()) restoreSession() }
    }

    suspend fun getProfile(): Result<UserProfile> = runCatching {
        val token = requireToken()
        api.getProfile("Bearer $token")
    }

    suspend fun updateProfile(update: ProfileUpdate): Result<UserProfile> = runCatching {
        val token = requireToken()
        val profile = api.updateProfile("Bearer $token", update)
        _user.value = profile
        profile
    }

    suspend fun syncFavorites(items: List<FavoriteSyncItem>): Result<SyncResponse> = runCatching {
        val token = requireToken()
        api.syncFavorites("Bearer $token", FavoriteSyncRequest(items))
    }

    fun requireToken(): String =
        store.accessToken ?: throw IllegalStateException("Not authenticated")
}
