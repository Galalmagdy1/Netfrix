package com.example.netfrix.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.netfrix.R
import com.example.netfrix.viewmodel.MoviesViewModel

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: MoviesViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val favorites by viewModel.favoriteMovies.collectAsState(initial = emptyList())

    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val searchBarAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "searchBarAlpha"
    )
    val searchBarOffsetY by animateFloatAsState(
        targetValue = if (isVisible) 0f else -50f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "searchBarOffsetY"
    )

    Scaffold(
        topBar = {
            SearchBar(
                query = searchQuery, 
                onQueryChange = viewModel::onSearchQueryChanged,
                modifier = Modifier.graphicsLayer {
                    alpha = searchBarAlpha
                    translationY = searchBarOffsetY
                }
            )
        },
        containerColor = Color.Transparent
    ) {
        Column(modifier = Modifier.padding(it)) {
            if (isSearching) {
                val loadingAlpha by animateFloatAsState(
                    targetValue = if (isVisible) 1f else 0f,
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                    label = "loadingAlpha"
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = loadingAlpha
                        },
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (errorMessage != null) {
                val errorAlpha by animateFloatAsState(
                    targetValue = if (isVisible) 1f else 0f,
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                    label = "errorAlpha"
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = errorAlpha
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(R.string.check_your_internet), color = Color.White)
                }
            } else if (searchQuery.isNotBlank() && searchResults.isEmpty()) {
                val noResultsAlpha by animateFloatAsState(
                    targetValue = if (isVisible) 1f else 0f,
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                    label = "noResultsAlpha"
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = noResultsAlpha
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(R.string.no_results_found_for, searchQuery), color = Color.White)
                }
            } else if (searchQuery.isBlank()) {
                val placeholderAlpha by animateFloatAsState(
                    targetValue = if (isVisible) 1f else 0f,
                    animationSpec = tween(durationMillis = 400, delayMillis = 200, easing = FastOutSlowInEasing),
                    label = "placeholderAlpha"
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = placeholderAlpha
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(R.string.search_for_movies_and_tv_shows), color = Color.White)
                }
            } else {
                val moviesWithFavoriteStatus = searchResults.map { movie ->
                    movie.copy(isFavorite = favorites.any { favMovie -> favMovie.id == movie.id })
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(moviesWithFavoriteStatus) { index, movie ->
                        val itemAlpha by animateFloatAsState(
                            targetValue = if (isVisible) 1f else 0f,
                            animationSpec = tween(
                                durationMillis = 500,
                                delayMillis = 300 + (index % 4) * 100,
                                easing = FastOutSlowInEasing
                            ),
                            label = "itemAlpha"
                        )
                        val itemScale by animateFloatAsState(
                            targetValue = if (isVisible) 1f else 0.8f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "itemScale"
                        )
                        MovieItem(
                            movie = movie,
                            onFavoriteClick = { viewModel.toggleFavorite(movie) },
                            onItemClick = { navController.navigate("detailscreen/${movie.id}") },
                            modifier = Modifier.graphicsLayer {
                                alpha = itemAlpha
                                scaleX = itemScale
                                scaleY = itemScale
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(text = stringResource(R.string.search), color = Color.White) },
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.DarkGray.copy(alpha = 0.5f),
            unfocusedContainerColor = Color.DarkGray.copy(alpha = 0.5f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White,
        ),
        singleLine = true,
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.clear_search), tint = Color.White)
                }
            }
        }
    )
}
