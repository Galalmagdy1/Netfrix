
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MoviesViewModel @Inject constructor(private val repository: MovieRepository) : ViewModel() {

    // Private flow for movies from the API
    private val _apiMovies = MutableStateFlow<List<MovieResult>>(emptyList())

    // Flow for favorite movies from the database
    val favoriteMovies: StateFlow<List<Movie>> = repository.getFavoriteMovies()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Public flow for the UI, combining API results with favorite status
    val movies: StateFlow<List<Movie>> = _apiMovies.combine(favoriteMovies) { apiMovies, favMovies ->
        val favIds = favMovies.map { it.id }.toSet()
        apiMovies.map { movieResult ->
            Movie(movieResult).copy(isFavorite = favIds.contains(movieResult.id))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


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
                _apiMovies.value = response.results // Update the private flow
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

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            val localMovie = repository.getMovieById(movie.id)
            if (localMovie != null) {
                repository.updateMovie(localMovie.copy(isFavorite = !localMovie.isFavorite))
            } else {
                repository.insertMovie(movie.copy(isFavorite = true))
            }
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
