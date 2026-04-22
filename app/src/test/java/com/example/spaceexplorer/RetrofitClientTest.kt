package com.example.spaceexplorer

import com.example.spaceexplorer.api.NasaApiService
import com.example.spaceexplorer.api.RetrofitClient
import org.junit.Assert.*
import org.junit.Test

class RetrofitClientTest {

    @Test
    fun `createNasaApiService returns non-null service`() {
        val service = RetrofitClient.createNasaApiService()
        assertNotNull(service)
    }

    @Test
    fun `base url uses HTTPS`() {
        assertTrue(NasaApiService.BASE_URL.startsWith("https://"))
    }

    @Test
    fun `base url points to nasa gov`() {
        assertTrue(NasaApiService.BASE_URL.contains("api.nasa.gov"))
    }

    @Test
    fun `createNasaApiService implements NasaApiService interface`() {
        val service = RetrofitClient.createNasaApiService()
        assertTrue(service is NasaApiService)
    }

    @Test
    fun `multiple calls return valid service instances`() {
        val s1 = RetrofitClient.createNasaApiService()
        val s2 = RetrofitClient.createNasaApiService()
        assertNotNull(s1)
        assertNotNull(s2)
    }
}
