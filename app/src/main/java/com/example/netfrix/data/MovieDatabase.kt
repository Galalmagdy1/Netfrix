package com.example.netfrix.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.netfrix.models.Movie

@Database(entities = [Movie::class], version = 6, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}
