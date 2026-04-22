package com.example.spaceexplorer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceexplorer.domain.model.SpaceResult
import com.example.spaceexplorer.domain.usecase.GetAPODUseCase
import com.example.spaceexplorer.domain.usecase.GetRecentAPODUseCase
import com.example.spaceexplorer.models.AstronomyPicture
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class LoadMode { RANDOM, RECENT }

data class SpaceUiState(
    val pictures: List<AstronomyPicture> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val loadMode: LoadMode = LoadMode.RANDOM,
    val selectedPicture: AstronomyPicture? = null,
    val isFromCache: Boolean = false
)

class SpaceViewModel(
    private val getAPOD: GetAPODUseCase,
    private val getRecentAPOD: GetRecentAPODUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpaceUiState())
    val uiState: StateFlow<SpaceUiState> = _uiState.asStateFlow()

    init { loadRandomAstronomyPictures() }

    fun loadRandomAstronomyPictures(count: Int = 20) {
        _uiState.update { it.copy(loadMode = LoadMode.RANDOM) }
        viewModelScope.launch {
            getAPOD(count).collect { result ->
                _uiState.update { state -> state.applyResult(result) }
            }
        }
    }

    fun loadRecentAstronomyPictures() {
        _uiState.update { it.copy(loadMode = LoadMode.RECENT) }
        viewModelScope.launch {
            val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val end = LocalDate.now()
            val start = end.minusDays(30)
            getRecentAPOD(start.format(fmt), end.format(fmt)).collect { result ->
                _uiState.update { state -> state.applyResult(result) }
            }
        }
    }

    fun selectPicture(picture: AstronomyPicture) =
        _uiState.update { it.copy(selectedPicture = picture) }

    fun clearError() = _uiState.update { it.copy(error = null) }

    fun refresh() = when (_uiState.value.loadMode) {
        LoadMode.RANDOM -> loadRandomAstronomyPictures()
        LoadMode.RECENT -> loadRecentAstronomyPictures()
    }

    private fun SpaceUiState.applyResult(result: SpaceResult<List<AstronomyPicture>>) = when (result) {
        is SpaceResult.Loading -> copy(isLoading = true, error = null)
        is SpaceResult.Success -> copy(pictures = result.data, isLoading = false, error = null, isFromCache = result.fromCache)
        is SpaceResult.Error   -> copy(isLoading = false, error = result.message)
    }
}
