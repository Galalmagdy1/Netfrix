package com.example.netfrix.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.netfrix.converters.ListConverter
import com.example.netfrix.models.Movie

@Database(entities = [Movie::class], version = 4, exportSchema = false)
@TypeConverters(ListConverter::class)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}
