    package com.example.netfrix.ui.ui.screens.details

    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.PaddingValues
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.width
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.LazyRow
    import androidx.compose.foundation.lazy.items
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.automirrored.filled.ArrowBack
    import androidx.compose.material.icons.filled.Favorite
    import androidx.compose.material.icons.filled.FavoriteBorder
    import androidx.compose.material.icons.filled.Star
    import androidx.compose.material3.Card
    import androidx.compose.material3.CardDefaults
    import androidx.compose.material3.CircularProgressIndicator
    import androidx.compose.material3.ExperimentalMaterial3Api
    import androidx.compose.material3.Icon
    import androidx.compose.material3.IconButton
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Scaffold
    import androidx.compose.material3.Text
    import androidx.compose.material3.TopAppBar
    import androidx.compose.material3.TopAppBarDefaults
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.getValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.blur
    import androidx.compose.ui.graphics.Brush
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.layout.ContentScale
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.text.style.TextAlign
    import androidx.compose.ui.text.style.TextOverflow
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.hilt.navigation.compose.hiltViewModel
    import androidx.navigation.NavController
    import coil.compose.AsyncImage
    import com.example.netfrix.viewmodel.MoviesViewModel

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MovieDetailScreen(
        navController: NavController,
        movieId: Int,
        viewModel: MoviesViewModel = hiltViewModel()
    ) {
        LaunchedEffect(movieId) {
            viewModel.getMovieDetails(movieId)
        }

        val movieDetails by viewModel.movieDetails.collectAsState()
        val favorites by viewModel.favoriteMovies.collectAsState(initial = emptyList())
        val isFavorite = favorites.any { it.id == movieId }
        val DarkBlue = Color(0xFF0D0C1D)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { /* No title for a cleaner look */ },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    //IconButton(onClick = { viewModel.run { toggleFavorite() } }) {
                    //                                Icon(
                    //                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    //                                    contentDescription = "Favorite",
                    //                                    tint = if (isFavorite) Color.Red else Color.White
                    //                                )
                    //                            }
                    actions = {
                        movieDetails?.let {

                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = DarkBlue // Set base background to DarkBlue
        ) { paddingValues ->
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                if (movieDetails == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Background Image (Blurred) - Draws behind everything
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w500${movieDetails?.backdropPath}",
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(radius = 16.dp)
                    )

                    // Fade-to-DarkBlue gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colorStops = arrayOf(
                                        0.4f to Color.Transparent, // Top part is clear
                                        0.65f to DarkBlue      // Fade to DarkBlue, then solid DarkBlue below
                                    )
                                )
                            )
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = paddingValues, // Apply padding for content to start below TopAppBar
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                        }

                        item {
                            // Centered Movie Poster
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(8.dp),
                                modifier = Modifier.height(280.dp)
                            ) {
                                AsyncImage(
                                    model = "https://image.tmdb.org/t/p/w500${movieDetails?.posterPath}",
                                    contentDescription = "Movie Poster",
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        item {
                            // Movie Title
                            Text(
                                text = movieDetails?.title ?: "",
                                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        item {
                            // Metadata Row
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color.Yellow)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = String.format("%.1f", movieDetails?.voteAverage), color = Color.White, fontSize = 16.sp)
                                }
                                Text(text = "|", color = Color.Gray)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = movieDetails?.releaseDate?.split("-")?.firstOrNull() ?: "", color = Color.White, fontSize = 16.sp)
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        item {
                            // Summary Section inside a Card
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.Transparent // Make card transparent to show gradient background
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Summary",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = movieDetails?.overview ?: "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.LightGray,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // Production Companies Section
                        item {
                            movieDetails?.productionCompanies?.takeIf { it.isNotEmpty() }?.let { companies ->
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Production Companies",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        contentPadding = PaddingValues(horizontal = 16.dp)
                                    ) {
                                        items(companies) { company ->
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.width(100.dp)
                                            ) {
                                                Card(
                                                    shape = RoundedCornerShape(8.dp),
                                                    colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.3f)),
                                                    modifier = Modifier.height(60.dp).fillMaxWidth()
                                                ) {
                                                    Box(
                                                        modifier = Modifier.fillMaxSize().padding(4.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        if (company.logoPath != null) {
                                                            AsyncImage(
                                                                model = "https://image.tmdb.org/t/p/w200${company.logoPath}",
                                                                contentDescription = company.name,
                                                                contentScale = ContentScale.Fit
                                                            )
                                                        } else {
                                                            Text(
                                                                text = company.name,
                                                                color = Color.White,
                                                                textAlign = TextAlign.Center,
                                                                fontSize = 12.sp,
                                                                maxLines = 2,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = company.name,
                                                    color = Color.LightGray,
                                                    fontSize = 12.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(32.dp)) // Add some padding at the bottom
                        }
                    }
                }
            }
        }
    }
