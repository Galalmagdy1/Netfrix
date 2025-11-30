package com.example.netfrix.data

import android.os.Parcelable
import com.example.netfrix.models.Movie
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class MovieDetails(
    val adult: Boolean,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    val genres: List<Genre>,
    val id: Int,
    @SerializedName("original_language")
    val originalLanguage: String,
    val overview: String?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("production_companies")
    val productionCompanies: List<ProductionCompany>,
    @SerializedName("release_date")
    val releaseDate: String,
    val runtime: Int?,
    val status: String,
    val title: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
) : Parcelable

@Parcelize
data class BelongsToCollection(
    val id: Int,
    val name: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?
) : Parcelable

@Parcelize
data class Genre(
    val id: Int,
    val name: String
) : Parcelable

@Parcelize
data class ProductionCompany(
    val id: Int,
    @SerializedName("logo_path")
    val logoPath: String?,
    val name: String,
    @SerializedName("origin_country")
    val originCountry: String
) : Parcelable

@Parcelize
data class ProductionCountry(
    @SerializedName("iso_3166_1")
    val iso31661: String,
    val name: String
) : Parcelable

@Parcelize
data class SpokenLanguage(
    @SerializedName("english_name")
    val englishName: String,
    @SerializedName("iso_639_1")
    val iso6391: String,
    val name: String
) : Parcelable

data class MovieListResponse(
    val page: Int,
    val results: List<MovieResult>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)

@Parcelize
data class MovieResult(
    val adult: Boolean,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("genre_ids")
    val genreIds: List<Int>,
    val id: Int,
    @SerializedName("original_language")
    val originalLanguage: String,
    @SerializedName("original_title")
    val originalTitle: String,
    val overview: String,
    val popularity: Double,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String,
    val title: String,
    val video: Boolean,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int
) : Parcelable {
    companion object {
        fun fromMovie(movie: Movie): MovieResult {
            return MovieResult(
                adult = false,
                backdropPath = movie.backdrop_path,
                genreIds = emptyList(),
                id = movie.id,
                originalLanguage = "",
                originalTitle = "",
                overview = movie.overview ?: "",
                popularity = 0.0,
                posterPath = movie.poster_path,
                releaseDate = movie.release_date ?: "",
                title = movie.title ?: "",
                video = false,
                voteAverage = movie.vote_average ?: 0.0,
                voteCount = 0
            )
        }
    }
}