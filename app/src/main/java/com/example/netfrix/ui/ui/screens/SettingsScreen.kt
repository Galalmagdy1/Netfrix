package com.example.netfrix.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.netfrix.R
import com.example.netfrix.notifications.NotificationHelper
import com.example.netfrix.ui.ui.screens.settings.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavController? = null,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val context = LocalContext.current

    val notificationsEnabled by settingsViewModel.notificationsEnabled.collectAsState()
    var currentLanguage by remember { mutableStateOf("English") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Dark Mode toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Dark Mode", fontSize = 18.sp)
            Switch(
                checked = isDarkMode,
                onCheckedChange = { settingsViewModel.toggleDarkMode() }
            )
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Notifications toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Notifications", fontSize = 18.sp)
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { newValue ->
                    settingsViewModel.toggleNotifications()
                    // Optionally send a test notification when enabled
                    if (newValue) {
                        NotificationHelper.sendNotification(
                            context = context,
                            title = "Notifications Enabled",
                            message = "You'll receive notifications from Netfrix!",
                            imageRes = R.mipmap.ic_launcher
                        )
                    }
                }
            )
        }
    }
}
