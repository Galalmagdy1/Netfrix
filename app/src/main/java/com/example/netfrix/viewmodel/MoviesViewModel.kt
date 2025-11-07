package com.example.netfrix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.netfrix.data.MovieDetails
import com.example.netfrix.data.MovieRepository
import com.example.netfrix.data.MovieResult
import com.example.netfrix.models.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MoviesViewModel @Inject constructor(private val repository: MovieRepository) : ViewModel() {

    private val _movies = MutableStateFlow<List<MovieResult>>(emptyList())
    val movies: StateFlow<List<MovieResult>> = _movies

    val favoriteMovies: StateFlow<List<Movie>> = repository.getFavoriteMovies()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _movieDetails = MutableStateFlow<MovieDetails?>(null)
    val movieDetails: StateFlow<MovieDetails?> = _movieDetails

    init {
        fetchMovies()
    }

    fun fetchMovies(isRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val page = if (isRefresh) Random.nextInt(1, 21) else 1
                val response = repository.getMovies(page)
                _movies.value = response.results
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getMovieDetails(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _movieDetails.value = repository.getMovieDetails(id)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(movieResult: MovieResult) {
        viewModelScope.launch {
            val localMovie = repository.getMovieById(movieResult.id)
            if (localMovie != null) {
                repository.updateMovie(localMovie.copy(isFavorite = !localMovie.isFavorite))
            } else {
                val newMovie = Movie(movieResult)
                repository.insertMovie(newMovie.copy(isFavorite = true))
            }
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            repository.updateMovie(movie.copy(isFavorite = !movie.isFavorite))
        }
    }

    fun toggleFavorite(movieDetails: MovieDetails) {
        viewModelScope.launch {
            val localMovie = repository.getMovieById(movieDetails.id)
            if (localMovie != null) {
                repository.updateMovie(localMovie.copy(isFavorite = !localMovie.isFavorite))
            } else {
                val newMovie = Movie(
                    id = movieDetails.id,
                    title = movieDetails.title,
                    overview = movieDetails.overview ?: "",
                    poster_path = movieDetails.posterPath,
                    backdrop_path = movieDetails.backdropPath,
                    release_date = movieDetails.releaseDate,
                    vote_average = movieDetails.voteAverage,
                    isFavorite = true
                )
                repository.insertMovie(newMovie)
            }
        }
    }
}