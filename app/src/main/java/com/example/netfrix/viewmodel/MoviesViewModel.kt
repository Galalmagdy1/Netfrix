package com.example.netfrix.viewmodel

import android.content.Context
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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@OptIn(FlowPreview::class)
@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repository: MovieRepository,
    private val firebaseAuth: FirebaseAuth,
    private val connectivityObserver: ConnectivityObserver,
    private val context: Context // هستخدمه للـ SharedPreferences
) : ViewModel() {

    private val _apiMovies = MutableStateFlow<List<MovieResult>>(emptyList())
    private var lastFavoriteAdded: Movie? = null

    fun getLastFavoriteAdded(): Movie? {
        // إذا فاضي بالذاكرة، استرجع من SharedPreferences
        if (lastFavoriteAdded == null) {
            val prefs = context.getSharedPreferences("movie_prefs", Context.MODE_PRIVATE)
            val id = prefs.getInt("last_fav_id", -1)
            val title = prefs.getString("last_fav_title", null)
            if (id != -1 && title != null) {
                lastFavoriteAdded = Movie(id = id, title = title, overview = "", poster_path = null, backdrop_path = null, release_date = null, vote_average = 0.0, isFavorite = true)
            }
        }
        return lastFavoriteAdded
    }

    fun setLastFavorite(movie: Movie) {
        lastFavoriteAdded = movie
        val prefs = context.getSharedPreferences("movie_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putInt("last_fav_id", movie.id)
            .putString("last_fav_title", movie.title)
            .apply()
    }

    fun clearLastFavorite() {
        lastFavoriteAdded = null
        val prefs = context.getSharedPreferences("movie_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove("last_fav_id").remove("last_fav_title").apply()
    }

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
        observeConnectivity()
        observeSearch()
    }

    private fun observeConnectivity() {
        connectivityObserver.observe()
            .onEach { status ->
                if (status == ConnectivityObserver.Status.Available && _errorMessage.value?.contains("No internet connection") == true) {
                    fetchMovies(isRefresh = true)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeSearch() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .collect { query ->
                    if (query.isNotBlank()) executeSearch(query)
                    else _searchResults.value = emptyList()
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
                _apiMovies.value = response.results
            } catch (e: Exception) {
                _errorMessage.value = "No internet connection. Displaying offline content."
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
            try { _movieDetails.value = repository.getMovieDetails(id) }
            catch (e: Exception) { _errorMessage.value = e.message }
            finally { _isLoading.value = false }
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            val localMovie = repository.getMovieById(movie.id)
            val isNowFavorite = localMovie?.let { !it.isFavorite } ?: true

            if (localMovie != null) repository.updateMovie(localMovie.copy(isFavorite = isNowFavorite))
            else repository.insertMovie(movie.copy(isFavorite = true))

            if (isNowFavorite) {
                setLastFavorite(movie)
                // Set flag to indicate a movie was recently added to favorites
                val prefs = context.getSharedPreferences("netfrix_prefs", Context.MODE_PRIVATE)
                prefs.edit().putBoolean("has_recent_favorite", true).apply()
            }
        }
    }

    fun logout() { firebaseAuth.signOut() }
}
