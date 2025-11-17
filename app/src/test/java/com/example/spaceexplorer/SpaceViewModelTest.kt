package com.example.spaceexplorer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.spaceexplorer.api.NasaApiService
import com.example.spaceexplorer.models.AstronomyPicture
import com.example.spaceexplorer.ui.viewmodels.SpaceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SpaceViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var nasaApiService: NasaApiService

    private lateinit var viewModel: SpaceViewModel

    private val samplePictures = listOf(
        AstronomyPicture(
            date = "2024-01-15",
            explanation = "A beautiful galaxy in the distant universe",
            hdUrl = "https://apod.nasa.gov/apod/image/2401/galaxy_hd.jpg",
            mediaType = "image",
            serviceVersion = "v1",
            title = "Spiral Galaxy NGC 1234",
            url = "https://apod.nasa.gov/apod/image/2401/galaxy.jpg",
            copyright = "NASA/ESA"
        ),
        AstronomyPicture(
            date = "2024-01-14",
            explanation = "Mars as seen from Earth",
            hdUrl = null,
            mediaType = "image",
            serviceVersion = "v1",
            title = "Mars at Opposition",
            url = "https://apod.nasa.gov/apod/image/2401/mars.jpg",
            copyright = null
        )
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadRandomAstronomyPictures success updates astronomyPictures`() = runTest {
        // Given
        whenever(nasaApiService.getAstronomyPictures(count = 20)).thenReturn(samplePictures)

        // When
        viewModel = SpaceViewModel(nasaApiService)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(samplePictures, viewModel.astronomyPictures.value)
        assertEquals(false, viewModel.isLoading.value)
        assertEquals("", viewModel.errorMessage.value)
    }

    @Test
    fun `loadRandomAstronomyPictures failure updates errorMessage`() = runTest {
        // Given
        whenever(nasaApiService.getAstronomyPictures(count = 20))
            .thenThrow(RuntimeException("Network error"))

        // When
        viewModel = SpaceViewModel(nasaApiService)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.errorMessage.value?.contains("Failed to load") == true)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `selectPicture updates selectedPicture`() = runTest {
        // Given
        whenever(nasaApiService.getAstronomyPictures(count = 20)).thenReturn(samplePictures)
        viewModel = SpaceViewModel(nasaApiService)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val pictureToSelect = samplePictures[0]
        viewModel.selectPicture(pictureToSelect)

        // Then
        assertEquals(pictureToSelect, viewModel.selectedPicture.value)
    }

    @Test
    fun `loadRecentAstronomyPictures success updates astronomyPictures`() = runTest {
        // Given
        whenever(nasaApiService.getAstronomyPictures(count = 20)).thenReturn(emptyList())
        whenever(
            nasaApiService.getAstronomyPicturesByDateRange(
                apiKey = org.mockito.kotlin.any(),
                startDate = org.mockito.kotlin.any(),
                endDate = org.mockito.kotlin.any()
            )
        ).thenReturn(samplePictures)

        viewModel = SpaceViewModel(nasaApiService)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.loadRecentAstronomyPictures()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(samplePictures, viewModel.astronomyPictures.value)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `isLoading is true during data fetch`() = runTest {
        // Given
        whenever(nasaApiService.getAstronomyPictures(count = 20)).thenReturn(samplePictures)

        // When
        viewModel = SpaceViewModel(nasaApiService)

        // Then - before coroutine completes
        assertEquals(true, viewModel.isLoading.value)

        testDispatcher.scheduler.advanceUntilIdle()

        // Then - after coroutine completes
        assertEquals(false, viewModel.isLoading.value)
    }
}
