package com.example.spaceexplorer.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceexplorer.data.auth.AuthRepository
import com.example.spaceexplorer.data.auth.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val user: UserProfile? = null,
    val error: String? = null
)

class AuthViewModel(private val repo: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repo.currentUser.collect { user ->
                _uiState.value = _uiState.value.copy(
                    user = user,
                    isAuthenticated = user != null
                )
            }
        }
        viewModelScope.launch { repo.restoreSession() }
    }

    fun register(email: String, password: String, displayName: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repo.register(email.trim(), password, displayName?.trim())
                .onFailure { _uiState.value = _uiState.value.copy(error = it.toMessage()) }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repo.login(email.trim(), password)
                .onFailure { _uiState.value = _uiState.value.copy(error = it.toMessage()) }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun logout() {
        viewModelScope.launch { repo.logout() }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun Throwable.toMessage(): String = when {
        message?.contains("409") == true -> "Email already registered."
        message?.contains("401") == true -> "Wrong email or password."
        message?.contains("Unable to resolve host") == true -> "No internet connection."
        else -> "Something went wrong. Please try again."
    }
}
