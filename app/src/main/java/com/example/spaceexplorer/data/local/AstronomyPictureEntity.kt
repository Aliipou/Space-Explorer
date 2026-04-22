package com.example.spaceexplorer.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spaceexplorer.models.AstronomyPicture

@Entity(tableName = "astronomy_pictures")
data class AstronomyPictureEntity(
    @PrimaryKey val date: String,
    val title: String,
    val explanation: String,
    @ColumnInfo(name = "hd_url") val hdUrl: String?,
    @ColumnInfo(name = "media_type") val mediaType: String,
    @ColumnInfo(name = "service_version") val serviceVersion: String,
    val url: String,
    val copyright: String?,
    @ColumnInfo(name = "cached_at") val cachedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): AstronomyPicture = AstronomyPicture(
        date = date,
        explanation = explanation,
        hdUrl = hdUrl,
        mediaType = mediaType,
        serviceVersion = serviceVersion,
        title = title,
        url = url,
        copyright = copyright
    )

    companion object {
        fun fromDomain(picture: AstronomyPicture): AstronomyPictureEntity =
            AstronomyPictureEntity(
                date = picture.date,
                title = picture.title,
                explanation = picture.explanation,
                hdUrl = picture.hdUrl,
                mediaType = picture.mediaType,
                serviceVersion = picture.serviceVersion ?: "v1",
                url = picture.url,
                copyright = picture.copyright
            )

        // Cache is valid for 24 hours
        const val CACHE_TTL_MS = 24 * 60 * 60 * 1000L
    }
}
