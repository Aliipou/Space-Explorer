package com.example.spaceexplorer.data.remote

import com.example.spaceexplorer.api.NasaApiService
import com.example.spaceexplorer.models.AstronomyPicture

class RemoteDataSource(private val api: NasaApiService) {

    suspend fun getAPOD(count: Int): List<AstronomyPicture> =
        api.getAstronomyPictures(count = count)

    suspend fun getAPODByDateRange(startDate: String, endDate: String): List<AstronomyPicture> =
        api.getAstronomyPicturesByDateRange(startDate = startDate, endDate = endDate)
}
