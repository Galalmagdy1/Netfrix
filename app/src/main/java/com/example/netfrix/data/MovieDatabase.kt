package com.example.netfrix.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.netfrix.models.Movie

@Database(entities = [Movie::class], version = 5, exportSchema = false)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}
