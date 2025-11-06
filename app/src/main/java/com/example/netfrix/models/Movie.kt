package com.example.netfrix.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String?,
    @SerializedName("release_date")
    val year: String?,
    @SerializedName("overview")
    val plot: String?,
    @SerializedName("poster_path")
    val posterPath: String?,
    val genre: String?,
    val director: String?,
    val actors: String?,
    val images: List<String>? = emptyList(),
    val rating: String?,
    val isFavorite: Boolean = false
)
{
    val poster: String
        get() = "https://image.tmdb.org/t/p/w500/$posterPath"
}
