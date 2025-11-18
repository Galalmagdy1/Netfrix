package com.example.netfrix

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.netfrix.navigation.NewGraph
import com.example.netfrix.notifications.NotificationHelper
import com.example.netfrix.ui.theme.NetfrixTheme
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        NotificationHelper.createNotificationChannel(this)

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (!isGranted) {
                    // You might want to show a warning here
                }
            }
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            val isDarkMode by mainViewModel.isDarkMode.collectAsState()

            NetfrixTheme(
                darkTheme = isDarkMode
            ) {
                NewGraph()
            }
        }
    }

    // When the user returns to the app
    override fun onResume() {
        super.onResume()
        NotificationHelper.sendNotification(
            context = applicationContext,
            title = "Welcome back!",
            message = "What do you want to watch today?"
        )
    }

    // When the user leaves the app
    override fun onPause() {
        super.onPause()

        // Get the last favorite from SharedPreferences
        val prefs = getSharedPreferences("netfrix_prefs", MODE_PRIVATE)
        val lastFavTitle = prefs.getString("last_fav_title", null)

        if (lastFavTitle != null) {
            NotificationHelper.sendNotification(
                context = applicationContext,
                title = "Favourite Reminder",
                message = "You have '$lastFavTitle' in your favourites 🍿"
            )
            // Clear the last favorite after the notification
            prefs.edit().remove("last_fav_title").apply()
        }
    }

}
