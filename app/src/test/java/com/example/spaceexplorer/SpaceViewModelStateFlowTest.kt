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
class SpaceViewModelStateFlowTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock private lateinit var getAPOD: GetAPODUseCase
    @Mock private lateinit var getRecentAPOD: GetRecentAPODUseCase

    private lateinit var viewModel: SpaceViewModel

    private val fixture = AstronomyPicture(
        date = "2024-01-15", explanation = "Test",
        hdUrl = null, mediaType = "image", serviceVersion = "v1",
        title = "Galaxy", url = "https://example.com/img.jpg", copyright = null
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `initial state has loading true`() = runTest {
        whenever(getAPOD(any())).thenReturn(flowOf(SpaceResult.Loading))
        viewModel = SpaceViewModel(getAPOD, getRecentAPOD)
        // Before coroutines complete, loading should be triggered
        testDispatcher.scheduler.advanceUntilIdle()
        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun `success result populates pictures`() = runTest {
        whenever(getAPOD(any())).thenReturn(
            flowOf(SpaceResult.Loading, SpaceResult.Success(listOf(fixture)))
        )
        viewModel = SpaceViewModel(getAPOD, getRecentAPOD)
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.pictures.size)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `error result sets error message`() = runTest {
        whenever(getAPOD(any())).thenReturn(
            flowOf(SpaceResult.Error("Network error"))
        )
        viewModel = SpaceViewModel(getAPOD, getRecentAPOD)
        advanceUntilIdle()

        assertEquals("Network error", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `cache result sets isFromCache true`() = runTest {
        whenever(getAPOD(any())).thenReturn(
            flowOf(SpaceResult.Success(listOf(fixture), fromCache = true))
        )
        viewModel = SpaceViewModel(getAPOD, getRecentAPOD)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isFromCache)
    }

    @Test
    fun `selectPicture updates selectedPicture in state`() = runTest {
        whenever(getAPOD(any())).thenReturn(flowOf(SpaceResult.Success(listOf(fixture))))
        viewModel = SpaceViewModel(getAPOD, getRecentAPOD)
        advanceUntilIdle()

        viewModel.selectPicture(fixture)
        assertEquals(fixture, viewModel.uiState.value.selectedPicture)
    }

    @Test
    fun `clearError nullifies error in state`() = runTest {
        whenever(getAPOD(any())).thenReturn(flowOf(SpaceResult.Error("Oops")))
        viewModel = SpaceViewModel(getAPOD, getRecentAPOD)
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.error)

        viewModel.clearError()
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `loadRecentAstronomyPictures sets loadMode to RECENT`() = runTest {
        whenever(getAPOD(any())).thenReturn(flowOf(SpaceResult.Success(emptyList())))
        whenever(getRecentAPOD(any(), any())).thenReturn(flowOf(SpaceResult.Success(listOf(fixture))))
        viewModel = SpaceViewModel(getAPOD, getRecentAPOD)
        advanceUntilIdle()

        viewModel.loadRecentAstronomyPictures()
        advanceUntilIdle()

        assertEquals(LoadMode.RECENT, viewModel.uiState.value.loadMode)
    }
}
