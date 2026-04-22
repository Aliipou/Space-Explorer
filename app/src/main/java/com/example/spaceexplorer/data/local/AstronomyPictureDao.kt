package com.example.spaceexplorer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AstronomyPictureDao {

    @Query("SELECT * FROM astronomy_pictures ORDER BY date DESC")
    suspend fun getAll(): List<AstronomyPictureEntity>

    @Query("SELECT * FROM astronomy_pictures WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): AstronomyPictureEntity?

    @Query("""
        SELECT * FROM astronomy_pictures
        WHERE date BETWEEN :startDate AND :endDate
        ORDER BY date DESC
    """)
    suspend fun getByDateRange(startDate: String, endDate: String): List<AstronomyPictureEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pictures: List<AstronomyPictureEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(picture: AstronomyPictureEntity)

    @Query("DELETE FROM astronomy_pictures")
    suspend fun deleteAll()

    @Query("""
        DELETE FROM astronomy_pictures
        WHERE cached_at < :expiryTime
    """)
    suspend fun deleteExpired(expiryTime: Long = System.currentTimeMillis() - AstronomyPictureEntity.CACHE_TTL_MS)

    @Query("SELECT COUNT(*) FROM astronomy_pictures")
    suspend fun count(): Int

    @Query("""
        SELECT COUNT(*) > 0 FROM astronomy_pictures
        WHERE cached_at > :freshnessThreshold
    """)
    suspend fun hasFreshCache(
        freshnessThreshold: Long = System.currentTimeMillis() - AstronomyPictureEntity.CACHE_TTL_MS
    ): Boolean
}
