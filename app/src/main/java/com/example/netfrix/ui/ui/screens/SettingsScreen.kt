package com.example.netfrix.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.netfrix.ui.ui.screens.settings.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavController? = null,
    viewModel: SettingsViewModel = viewModel()
) {
    val isDarkMode = viewModel.isDarkMode.collectAsState()

    var notificationsEnabled by remember { mutableStateOf(true) }
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
                checked = isDarkMode.value,
                onCheckedChange = { viewModel.toggleDarkMode() }
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
                onCheckedChange = { notificationsEnabled = it }
            )
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Language selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    currentLanguage = if (currentLanguage == "English") "Arabic" else "English"
                }
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Language", fontSize = 18.sp)
            Text(text = currentLanguage, color = Color.Gray)
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Logout button
        Button(
            onClick = {
                // Implement logout later
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Logout", fontSize = 18.sp)
        }
    }
}
