package com.example.spaceexplorer.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.spaceexplorer.data.remote.RemoteDataSource
import com.example.spaceexplorer.models.AstronomyPicture
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * PagingSource for paginated APOD browsing by date range.
 * Each page loads [pageSize] days, walking backwards from today.
 */
class ApodPagingSource(
    private val remote: RemoteDataSource
) : PagingSource<String, AstronomyPicture>() {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun getRefreshKey(state: PagingState<String, AstronomyPicture>): String? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey
                ?: state.closestPageToPosition(anchor)?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, AstronomyPicture> {
        return try {
            val endDateStr = params.key ?: formatter.format(LocalDate.now())
            val endDate = LocalDate.parse(endDateStr, formatter)
            val startDate = endDate.minusDays((params.loadSize - 1).toLong())

            val pictures = remote.getAPODByDateRange(
                startDate = formatter.format(startDate),
                endDate = endDateStr
            ).sortedByDescending { it.date }

            val prevKey = if (pictures.size < params.loadSize) null
            else formatter.format(startDate.minusDays(1))

            LoadResult.Page(
                data = pictures,
                prevKey = prevKey,
                nextKey = null // We paginate backwards (newest first), no forward
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
