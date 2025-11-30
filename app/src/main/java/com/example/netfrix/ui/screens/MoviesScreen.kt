package com.example.netfrix.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.netfrix.R
import com.example.netfrix.viewmodel.AuthViewModel
import com.example.netfrix.viewmodel.MoviesViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(
    navController: NavController,
    viewModel: MoviesViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var showMenu by remember { mutableStateOf(false) }
    val movies by viewModel.movies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var showRefreshBanner by remember { mutableStateOf(false) }

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)
    val refreshMessage = stringResource(R.string.refresh_message)

    LaunchedEffect(Unit) {
        showRefreshBanner = true
        delay(3000)
        showRefreshBanner = false
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(top = 0.dp),
                title = { Text(text = stringResource(R.string.movies), color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.more))
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.logout)) },
                            onClick = {
                                authViewModel.signout()
                                navController.navigate("login") {
                                    popUpTo("main") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = {
                    viewModel.fetchMovies(isRefresh = true)
                    coroutineScope.launch {
                        showRefreshBanner = true
                        delay(3000)
                        showRefreshBanner = false
                    }
                },
            ) {
                if (errorMessage != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = stringResource(R.string.check_your_internet), color = Color.White)
                    }
                } else {
                    Column {
                        AnimatedVisibility(
                            visible = showRefreshBanner,
                            enter = slideInVertically { fullHeight -> -fullHeight } + fadeIn(),
                            exit = slideOutVertically { fullHeight -> -fullHeight } + fadeOut()
                        ) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = Color(0xFF1E88E5),
                                tonalElevation = 4.dp
                            ) {
                                Text(
                                    text = refreshMessage,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                    color = Color.White
                                )
                            }
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(movies) { movie ->
                                MovieItem(
                                    movie = movie,
                                    onFavoriteClick = {
                                        if (!movie.isFavorite) {
                                            viewModel.setLastFavorite(movie)
                                        }
                                        viewModel.toggleFavorite(movie)
                                    },
                                    onItemClick = { navController.navigate("detailscreen/${movie.id}") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
