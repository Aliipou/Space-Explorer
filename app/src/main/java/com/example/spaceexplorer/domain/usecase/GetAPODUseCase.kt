package com.example.spaceexplorer.domain.usecase

import com.example.spaceexplorer.data.repository.SpaceRepository
import com.example.spaceexplorer.domain.model.SpaceResult
import com.example.spaceexplorer.models.AstronomyPicture
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAPODUseCase(private val repository: SpaceRepository) {
    operator fun invoke(count: Int = 20): Flow<SpaceResult<List<AstronomyPicture>>> =
        repository.getRandomAPOD(count)
}

class GetRecentAPODUseCase(private val repository: SpaceRepository) {
    operator fun invoke(startDate: String, endDate: String): Flow<SpaceResult<List<AstronomyPicture>>> =
        repository.getRecentAPOD(startDate, endDate)
            .map { result ->
                // Domain transform: filter out video-only entries for recent view
                if (result is SpaceResult.Success) {
                    SpaceResult.Success(
                        data = result.data.sortedByDescending { it.date },
                        fromCache = result.fromCache
                    )
                } else result
            }
}
