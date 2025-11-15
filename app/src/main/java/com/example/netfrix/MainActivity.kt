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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.netfrix.navigation.NewGraph
import com.example.netfrix.notifications.NotificationHelper
import com.example.netfrix.ui.ui.screens.settings.SettingsViewModel
import com.example.netfrix.viewmodel.MoviesViewModel
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val moviesViewModel: MoviesViewModel by viewModels()
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        NotificationHelper.createNotificationChannel(this)

        // طلب صلاحية الإشعارات لأندرويد 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (!isGranted) {
                    // ممكن تعرض تحذير هنا
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
                NewGraph(settingsViewModel = settingsViewModel)
            }
        }
    }

    // لما المستخدم يرجع للتطبيق
    override fun onResume() {
        super.onResume()
        NotificationHelper.sendNotification(
            context = applicationContext,
            title = "Welcome back!",
            message = "What do you want to watch today?"
        )
    }

    // لما المستخدم يخرج من التطبيق
    override fun onPause() {
        super.onPause()

        // جلب آخر مفضل من SharedPreferences
        val prefs = getSharedPreferences("netfrix_prefs", MODE_PRIVATE)
        val lastFavTitle = prefs.getString("last_fav_title", null)

        if (lastFavTitle != null) {
            NotificationHelper.sendNotification(
                context = applicationContext,
                title = "Favourite Reminder",
                message = "You have '$lastFavTitle' in your favourites 🍿"
            )
            // امسح آخر مفضل بعد الإشعار
            prefs.edit().remove("last_fav_title").apply()
        }
    }

}
