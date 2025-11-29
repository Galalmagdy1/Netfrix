package com.example.netfrix.data

import com.example.netfrix.network.MovieService
import com.example.netfrix.models.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val movieService: MovieService,
    private val movieDao: MovieDao
) {

    suspend fun getMovies(page: Int) = movieService.getMovies(page)

    suspend fun getMovieDetails(id: Int) = movieService.getMovieDetails(id)

    suspend fun searchMovies(query: String) = movieService.searchMovies(query)

    fun getFavoriteMovies(): Flow<List<Movie>> = movieDao.getFavoriteMovies()

    fun getAllMovies(): Flow<List<Movie>> = movieDao.getAllMovies()

    suspend fun updateMovie(movie: Movie) = movieDao.updateMovie(movie)

    suspend fun getMovieById(id: Int): Movie? = movieDao.getMovieById(id)

    suspend fun insertMovie(movie: Movie) = movieDao.insert(movie)
    suspend fun getFavoriteMoviesList(): List<Movie> {
        return getFavoriteMovies().first()
    }
}