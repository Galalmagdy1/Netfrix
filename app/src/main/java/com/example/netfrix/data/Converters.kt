package com.example.netfrix.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromGenreList(value: List<Genre>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toGenreList(value: String): List<Genre>? {
        val listType = object : TypeToken<List<Genre>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromProductionCompanyList(value: List<ProductionCompany>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toProductionCompanyList(value: String): List<ProductionCompany>? {
        val listType = object : TypeToken<List<ProductionCompany>?>() {}.type
        return Gson().fromJson(value, listType)
    }
}
