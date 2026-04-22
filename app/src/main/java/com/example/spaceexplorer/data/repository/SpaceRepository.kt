package com.example.spaceexplorer.data.repository

import com.example.spaceexplorer.domain.model.SpaceResult
import com.example.spaceexplorer.models.AstronomyPicture
import kotlinx.coroutines.flow.Flow

interface SpaceRepository {
    fun getRandomAPOD(count: Int = 20): Flow<SpaceResult<List<AstronomyPicture>>>
    fun getRecentAPOD(startDate: String, endDate: String): Flow<SpaceResult<List<AstronomyPicture>>>
    suspend fun clearCache()
}
