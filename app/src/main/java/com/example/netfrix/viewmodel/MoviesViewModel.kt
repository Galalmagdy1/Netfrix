
package com.example.netfrix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.netfrix.data.MovieDetails
import com.example.netfrix.data.MovieRepository
import com.example.netfrix.data.MovieResult
import com.example.netfrix.models.Movie
import com.example.netfrix.network.ConnectivityObserver
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@OptIn(FlowPreview::class)
@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repository: MovieRepository,
    private val firebaseAuth: FirebaseAuth,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _apiMovies = MutableStateFlow<List<MovieResult>>(emptyList())

    val favoriteMovies: StateFlow<List<Movie>> = repository.getFavoriteMovies()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    // --- Search State ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _searchResults = MutableStateFlow<List<MovieResult>>(emptyList())
    val searchResults: StateFlow<List<Movie>> = _searchResults.combine(favoriteMovies) { searchResults, favMovies ->
        val favIds = favMovies.map { it.id }.toSet()
        searchResults.map { movieResult ->
            Movie(movieResult).copy(isFavorite = favIds.contains(movieResult.id))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    // --- End Search State ---


    init {
        fetchMovies()

        connectivityObserver.observe()
            .onEach { status ->
                if (status == ConnectivityObserver.Status.Available && _errorMessage.value?.contains("No internet connection") == true) {
                    fetchMovies(isRefresh = true)
                }
            }
            .launchIn(viewModelScope)

        // Debounce search query
        viewModelScope.launch {
            _searchQuery
                .debounce(500) // 500ms debounce
                .collect { query ->
                    if (query.isNotBlank()) {
                        executeSearch(query)
                    } else {
                        _searchResults.value = emptyList() // Clear results if query is empty
                    }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    private fun executeSearch(query: String) {
        viewModelScope.launch {
            _isSearching.value = true
            _errorMessage.value = null
            try {
                val response = repository.searchMovies(query)
                _searchResults.value = response.results
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch search results: ${e.message}"
            } finally {
                _isSearching.value = false
            }
        }
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
                _errorMessage.value = "No internet connection. Displaying offline content."
                // Fallback to local data
                val localFavorites = repository.getFavoriteMovies().first()
                if (localFavorites.isNotEmpty()) {
                    _apiMovies.value = localFavorites.map { MovieResult.fromMovie(it) }
                } else {
                    val allMovies = repository.getAllMovies().first()
                    _apiMovies.value = allMovies.map { MovieResult.fromMovie(it) }
                }
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

    fun logout() {
        firebaseAuth.signOut()
    }
}
