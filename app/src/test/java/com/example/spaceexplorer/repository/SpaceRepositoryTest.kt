package com.example.spaceexplorer.repository

import com.example.spaceexplorer.data.local.AstronomyPictureDao
import com.example.spaceexplorer.data.local.AstronomyPictureEntity
import com.example.spaceexplorer.data.remote.RemoteDataSource
import com.example.spaceexplorer.data.repository.SpaceRepositoryImpl
import com.example.spaceexplorer.domain.model.SpaceResult
import com.example.spaceexplorer.models.AstronomyPicture
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SpaceRepositoryTest {

    @Mock private lateinit var remoteDataSource: RemoteDataSource
    @Mock private lateinit var dao: AstronomyPictureDao

    private lateinit var repository: SpaceRepositoryImpl

    private val fixture = AstronomyPicture(
        date = "2024-01-15", explanation = "Test",
        hdUrl = null, mediaType = "image", serviceVersion = "v1",
        title = "Test Galaxy", url = "https://example.com/img.jpg", copyright = null
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = SpaceRepositoryImpl(remoteDataSource, dao)
    }

    @Test
    fun `getRandomAPOD emits Loading then Success`() = runTest {
        whenever(dao.getAll()).thenReturn(emptyList())
        whenever(remoteDataSource.getAPOD(any())).thenReturn(listOf(fixture))

        val results = repository.getRandomAPOD(1).toList()

        assertTrue(results[0] is SpaceResult.Loading)
        assertTrue(results.last() is SpaceResult.Success)
        assertEquals(1, (results.last() as SpaceResult.Success).data.size)
    }

    @Test
    fun `getRandomAPOD serves cache first then remote`() = runTest {
        val cached = listOf(AstronomyPictureEntity.fromDomain(fixture))
        whenever(dao.getAll()).thenReturn(cached)
        whenever(remoteDataSource.getAPOD(any())).thenReturn(listOf(fixture))

        val results = repository.getRandomAPOD(1).toList()

        // Loading → cached success → fresh success
        assertTrue(results[0] is SpaceResult.Loading)
        val cacheResult = results[1] as SpaceResult.Success
        assertTrue(cacheResult.fromCache)
        val freshResult = results[2] as SpaceResult.Success
        assertFalse(freshResult.fromCache)
    }

    @Test
    fun `getRandomAPOD emits Error when remote fails and no cache`() = runTest {
        whenever(dao.getAll()).thenReturn(emptyList())
        whenever(remoteDataSource.getAPOD(any())).thenThrow(RuntimeException("Network failure"))

        val results = repository.getRandomAPOD(1).toList()

        assertTrue(results.last() is SpaceResult.Error)
        assertTrue((results.last() as SpaceResult.Error).message.contains("Network failure") ||
                   (results.last() as SpaceResult.Error).message.isNotEmpty())
    }

    @Test
    fun `getRandomAPOD swallows remote error when cache available`() = runTest {
        val cached = listOf(AstronomyPictureEntity.fromDomain(fixture))
        whenever(dao.getAll()).thenReturn(cached)
        whenever(remoteDataSource.getAPOD(any())).thenThrow(RuntimeException("Network failure"))

        val results = repository.getRandomAPOD(1).toList()

        // Should NOT end with Error because cache was available
        assertFalse(results.last() is SpaceResult.Error)
        assertTrue(results.any { it is SpaceResult.Success })
    }

    @Test
    fun `getRandomAPOD inserts fresh data into Room`() = runTest {
        whenever(dao.getAll()).thenReturn(emptyList())
        whenever(remoteDataSource.getAPOD(any())).thenReturn(listOf(fixture))

        repository.getRandomAPOD(1).toList()

        verify(dao).insertAll(any())
    }

    @Test
    fun `clearCache delegates to dao deleteAll`() = runTest {
        repository.clearCache()
        verify(dao).deleteAll()
    }
}
