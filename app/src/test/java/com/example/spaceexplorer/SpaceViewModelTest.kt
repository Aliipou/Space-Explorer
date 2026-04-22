package com.example.spaceexplorer

import com.example.spaceexplorer.domain.model.SpaceResult
import com.example.spaceexplorer.domain.usecase.GetAPODUseCase
import com.example.spaceexplorer.domain.usecase.GetRecentAPODUseCase
import com.example.spaceexplorer.models.AstronomyPicture
import com.example.spaceexplorer.ui.viewmodels.LoadMode
import com.example.spaceexplorer.ui.viewmodels.SpaceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SpaceViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock private lateinit var getAPOD: GetAPODUseCase
    @Mock private lateinit var getRecentAPOD: GetRecentAPODUseCase

    private lateinit var viewModel: SpaceViewModel

    private val fixture = AstronomyPicture(
        date = "2024-01-15",
        explanation = "A beautiful galaxy in the distant universe",
        hdUrl = "https://apod.nasa.gov/apod/image/2401/galaxy_hd.jpg",
        mediaType = "image",
        serviceVersion = "v1",
        title = "Spiral Galaxy NGC 1234",
        url = "https://apod.nasa.gov/apod/image/2401/galaxy.jpg",
        copyright = "NASA/ESA"
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `load success populates pictures in StateFlow`() = runTest {
        whenever(getAPOD(any())).thenReturn(
            flowOf(SpaceResult.Loading, SpaceResult.Success(listOf(fixture)))
        )
        viewModel = SpaceViewModel(getAPOD, getRecentAPOD)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.pictures.size)
        assertEquals(fixture, state.pictures.first())
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `load failure sets error in StateFlow`() = runTest {
        whenever(getAPOD(any())).thenReturn(
            flowOf(SpaceResult.Error("Network error"))
        )
        viewModel = SpaceViewModel(getAPOD, getRecentAPOD)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("Network error"))
        assertFalse(state.isLoading)
    }

    @Test
    fun `isLoading is true during Loading emission`() = runTest {
        whenever(getAPOD(any())).thenReturn(flowOf(SpaceResult.Loading))
        viewModel = SpaceViewModel(getAPOD, getRecentAPOD)
        // Before idle, loading state is driven by the flow
        testDispatcher.scheduler.runCurrent()
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `selectPicture updates selectedPicture in StateFlow`() = runTest {
        whenever(getAPOD(any())).thenReturn(flowOf(SpaceResult.Success(listOf(fixture))))
        viewModel = SpaceViewModel(getAPOD, getRecentAPOD)
        advanceUntilIdle()

        viewModel.selectPicture(fixture)
        assertEquals(fixture, viewModel.uiState.value.selectedPicture)
    }

    @Test
    fun `loadRecentAstronomyPictures sets RECENT mode`() = runTest {
        whenever(getAPOD(any())).thenReturn(flowOf(SpaceResult.Success(emptyList())))
        whenever(getRecentAPOD(any(), any())).thenReturn(
            flowOf(SpaceResult.Success(listOf(fixture)))
        )
        viewModel = SpaceViewModel(getAPOD, getRecentAPOD)
        advanceUntilIdle()

        viewModel.loadRecentAstronomyPictures()
        advanceUntilIdle()

        assertEquals(LoadMode.RECENT, viewModel.uiState.value.loadMode)
        assertEquals(1, viewModel.uiState.value.pictures.size)
    }
}
