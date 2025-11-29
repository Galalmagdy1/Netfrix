package com.example.netfrix

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.example.netfrix.data.MovieRepository
import com.example.netfrix.navigation.NewGraph
import com.example.netfrix.NotificationHelper
import com.example.netfrix.viewmodel.SettingsViewModel
import com.example.netfrix.viewmodel.MoviesViewModel
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val moviesViewModel: MoviesViewModel by viewModels()
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>

    @Inject
    lateinit var movieRepository: MovieRepository

    private val notificationHandler = Handler(Looper.getMainLooper())
    private val openFavoritesState = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        NotificationHelper.createNotificationChannel(this)
        handleNotificationIntent(intent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (!isGranted) {
                }
            }
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

            MaterialTheme(
                colorScheme = if (isDarkMode) darkColorScheme() else lightColorScheme()
            ) {
                NewGraph(
                    settingsViewModel = settingsViewModel,
                    openFavorites = openFavoritesState.value,
                    onFavoritesHandled = { openFavoritesState.value = false }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        // Check if notifications are enabled before sending
        val prefs = getSharedPreferences("netfrix_prefs", MODE_PRIVATE)
        val notificationsEnabled = prefs.getBoolean("notifications_enabled", true)
        if (notificationsEnabled) {
            NotificationHelper.sendNotification(
                context = applicationContext,
                title = "Welcome back!",
                message = "What do you want to watch today?"
            )
        }
    }

    override fun onStop() {
        super.onStop()

        val prefs = getSharedPreferences("netfrix_prefs", MODE_PRIVATE)
        val hasRecentFavorite = prefs.getBoolean("has_recent_favorite", false)

        if (hasRecentFavorite) {
            lifecycleScope.launch {
                try {
                    val favoriteMovies = movieRepository.getFavoriteMoviesList()

                    if (favoriteMovies.isNotEmpty()) {
                        notificationHandler.postDelayed({
                            // Check if notifications are enabled before sending
                            val notificationsEnabled = prefs.getBoolean("notifications_enabled", true)
                            if (notificationsEnabled) {
                                val count = favoriteMovies.size
                                val message = if (count == 1) {
                                    "You have '${favoriteMovies.first().title}' in your favourites. Come and watch it! 🍿"
                                } else {
                                    "You have $count movies in your favourites. Come and watch them back! 🍿"
                                }

                                NotificationHelper.sendNotification(
                                    context = applicationContext,
                                    title = "Favourite Reminder",
                                    message = message,
                                    openFavorites = true
                                )
                            }
                            
                            prefs.edit().putBoolean("has_recent_favorite", false).apply()
                        }, 2000)
                    } else {
                        prefs.edit().putBoolean("has_recent_favorite", false).apply()
                    }
                } catch (e: Exception) {
                    prefs.edit().putBoolean("has_recent_favorite", false).apply()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationHandler.removeCallbacksAndMessages(null)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        if (intent == null) return
        if (intent.getBooleanExtra("open_favorites", false)) {
            openFavoritesState.value = true
            intent.removeExtra("open_favorites")
        }
    }

}
