package com.example.spaceexplorer.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceexplorer.api.NasaApiService
import com.example.spaceexplorer.models.AstronomyPicture
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * ViewModel to manage space-related data for UI
 */
class SpaceViewModel(private val nasaApiService: NasaApiService) : ViewModel() {

    // LiveData for holding astronomy pictures
    private val _astronomyPictures = MutableLiveData<List<AstronomyPicture>>()
    val astronomyPictures: LiveData<List<AstronomyPicture>> = _astronomyPictures

    // LiveData for error messages
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Selected picture for detail view
    private val _selectedPicture = MutableLiveData<AstronomyPicture>()
    val selectedPicture: LiveData<AstronomyPicture> = _selectedPicture

    /**
     * Initialize ViewModel and load initial data
     */
    init {
        loadRandomAstronomyPictures()
    }

    /**
     * Load a random set of astronomy pictures
     * @param count Number of pictures to fetch
     */
    fun loadRandomAstronomyPictures(count: Int = 20) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val pictures = nasaApiService.getAstronomyPictures(count = count)
                _astronomyPictures.value = pictures
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load astronomy pictures: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Load astronomy pictures from the past month
     */
    fun loadRecentAstronomyPictures() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val endDate = LocalDate.now()
                val startDate = endDate.minusDays(30)
                
                val pictures = nasaApiService.getAstronomyPicturesByDateRange(
                    startDate = startDate.format(formatter),
                    endDate = endDate.format(formatter)
                )
                
                _astronomyPictures.value = pictures
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load recent astronomy pictures: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Set the selected picture for the detail view
     * @param picture The astronomy picture to display in detail
     */
    fun selectPicture(picture: AstronomyPicture) {
        _selectedPicture.value = picture
    }
}