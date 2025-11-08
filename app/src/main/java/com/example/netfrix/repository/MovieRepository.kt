package com.example.netfrix.repository

import com.example.netfrix.data.MovieDao
import com.example.netfrix.network.MovieApiService
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val movieDao: MovieDao,
    private val movieApiService: MovieApiService
) {
    val movies = movieDao.getAllMovies()
    val favoriteMovies = movieDao.getFavoriteMovies()

    suspend fun refreshMovies() {
        val moviesFromApi = movieApiService.getMovies().movies
        movieDao.insertAll(moviesFromApi)
    }

    suspend fun updateMovie(movie: com.example.netfrix.models.Movie) {
        movieDao.updateMovie(movie)
    }

    suspend fun getMovieById(id: Int): com.example.netfrix.models.Movie? {
        return movieDao.getMovieById(id)
    }
}
