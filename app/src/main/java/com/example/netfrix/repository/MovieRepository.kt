package com.example.netfrix.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.netfrix.data.MovieDao
import com.example.netfrix.models.Movie
import com.example.netfrix.network.MovieApiService
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext

class MovieRepository @Inject constructor(
    private val movieDao: MovieDao,
    private val movieApiService: MovieApiService,
    @ApplicationContext private val context: Context
) {


    private val prefs: SharedPreferences = context.getSharedPreferences("netfrix_prefs", Context.MODE_PRIVATE)


    val movies = movieDao.getAllMovies()
    val favoriteMovies = movieDao.getFavoriteMovies()


    suspend fun refreshMovies() {
        val moviesFromApi = movieApiService.getMovies().movies
        movieDao.insertAll(moviesFromApi)
    }


    suspend fun updateMovie(movie: Movie) {
        movieDao.updateMovie(movie)
    }


    suspend fun getMovieById(id: Int): Movie? {
        return movieDao.getMovieById(id)
    }


    fun saveLastFavorite(movie: Movie) {
        prefs.edit()
            .putInt("last_fav_id", movie.id)
            .putString("last_fav_title", movie.title)
            .apply()
    }

    fun getLastFavorite(): Movie? {
        val id = prefs.getInt("last_fav_id", -1)
        val title = prefs.getString("last_fav_title", null)
        return if (id != -1 && title != null) {
            Movie(
                id = id,
                title = title,
                overview = "",
                poster_path = null,
                backdrop_path = null,
                release_date = "",
                vote_average = 0.0,
                isFavorite = true
            )
        } else null
    }

    fun clearLastFavorite() {
        prefs.edit().remove("last_fav_id").remove("last_fav_title").apply()
    }
}
