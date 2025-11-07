package com.example.netfrix.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.netfrix.data.MovieResult
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String?,
    val backdrop_path: String?,
    val release_date: String?,
    val vote_average: Double?,
    var isFavorite: Boolean = false
) : Parcelable {
    constructor(movieResult: MovieResult) : this(
        id = movieResult.id,
        title = movieResult.title,
        overview = movieResult.overview,
        poster_path = movieResult.posterPath,
        backdrop_path = movieResult.backdropPath,
        release_date = movieResult.releaseDate,
        vote_average = movieResult.voteAverage
    )
}
