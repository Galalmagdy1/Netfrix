package com.example.netfrix.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.netfrix.data.Genre
import com.example.netfrix.data.MovieResult
import com.example.netfrix.data.ProductionCompany
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
    var isFavorite: Boolean = false,
    val genres: List<Genre> = emptyList(),
    val runtime: Int? = null,
    val status: String = "",
    val original_language: String = "",
    val adult: Boolean = false,
    val production_companies: List<ProductionCompany> = emptyList()
) : Parcelable {
    constructor(movieResult: MovieResult) : this(
        id = movieResult.id,
        title = movieResult.title,
        overview = movieResult.overview,
        poster_path = movieResult.posterPath,
        backdrop_path = movieResult.backdropPath,
        release_date = movieResult.releaseDate,
        vote_average = movieResult.voteAverage,
        isFavorite = false,
        genres = emptyList(),
        runtime = null,
        status = "",
        original_language = "",
        adult = false,
        production_companies = emptyList()
    )
}
