package com.example.spaceexplorer.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AstronomyPictureEntity::class],
    version = 1,
    exportSchema = true
)
abstract class SpaceDatabase : RoomDatabase() {

    abstract fun astronomyPictureDao(): AstronomyPictureDao

    companion object {
        private const val DATABASE_NAME = "space_explorer.db"

        @Volatile
        private var INSTANCE: SpaceDatabase? = null

        fun getInstance(context: Context): SpaceDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SpaceDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
