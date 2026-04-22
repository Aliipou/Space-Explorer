package com.example.spaceexplorer.data.repository

import com.example.spaceexplorer.data.local.AstronomyPictureDao
import com.example.spaceexplorer.data.local.AstronomyPictureEntity
import com.example.spaceexplorer.data.remote.RemoteDataSource
import com.example.spaceexplorer.domain.model.SpaceResult
import com.example.spaceexplorer.models.AstronomyPicture
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SpaceRepositoryImpl(
    private val remote: RemoteDataSource,
    private val dao: AstronomyPictureDao
) : SpaceRepository {

    override fun getRandomAPOD(count: Int): Flow<SpaceResult<List<AstronomyPicture>>> = flow {
        emit(SpaceResult.Loading)

        // 1. Serve stale-while-revalidate: emit cache immediately if available
        val cached = dao.getAll().map { it.toDomain() }
        if (cached.isNotEmpty()) {
            emit(SpaceResult.Success(cached, fromCache = true))
        }

        // 2. Fetch fresh data with exponential backoff retry
        runCatching {
            withRetry(maxAttempts = 3, baseDelayMs = 1_000L) {
                remote.getAPOD(count)
            }
        }.onSuccess { fresh ->
            dao.insertAll(fresh.map { AstronomyPictureEntity.fromDomain(it) })
            emit(SpaceResult.Success(fresh, fromCache = false))
        }.onFailure { error ->
            // Only emit error if we had no cached data to show
            if (cached.isEmpty()) {
                emit(SpaceResult.Error(
                    message = error.message ?: "Failed to load pictures",
                    cause = error
                ))
            }
            // If cache was served, silently swallow — user already has data
        }
    }

    override fun getRecentAPOD(startDate: String, endDate: String): Flow<SpaceResult<List<AstronomyPicture>>> = flow {
        emit(SpaceResult.Loading)

        val cached = dao.getByDateRange(startDate, endDate).map { it.toDomain() }
        if (cached.isNotEmpty()) {
            emit(SpaceResult.Success(cached, fromCache = true))
        }

        runCatching {
            withRetry(maxAttempts = 3, baseDelayMs = 1_000L) {
                remote.getAPODByDateRange(startDate, endDate)
            }
        }.onSuccess { fresh ->
            dao.insertAll(fresh.map { AstronomyPictureEntity.fromDomain(it) })
            emit(SpaceResult.Success(fresh.sortedByDescending { it.date }, fromCache = false))
        }.onFailure { error ->
            if (cached.isEmpty()) {
                emit(SpaceResult.Error(error.message ?: "Failed to load recent pictures", error))
            }
        }
    }

    override suspend fun clearCache() {
        dao.deleteAll()
    }
}

// MARK: - Exponential backoff retry

private suspend fun <T> withRetry(
    maxAttempts: Int = 3,
    baseDelayMs: Long = 1_000L,
    maxDelayMs: Long = 16_000L,
    block: suspend () -> T
): T {
    var lastException: Throwable? = null
    repeat(maxAttempts) { attempt ->
        try {
            return block()
        } catch (e: Exception) {
            lastException = e
            if (attempt < maxAttempts - 1) {
                val delay = minOf(baseDelayMs * (1L shl attempt), maxDelayMs)
                kotlinx.coroutines.delay(delay)
            }
        }
    }
    throw lastException ?: IllegalStateException("Retry exhausted")
}
