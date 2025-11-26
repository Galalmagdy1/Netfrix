package com.example.netfrix.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
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
import com.example.netfrix.NotificationHelper
import com.example.netfrix.ui.ui.screens.Screen
import com.example.netfrix.ui.ui.screens.settings.SettingsViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    val notificationsEnabled by settingsViewModel.notificationsEnabled.collectAsState()
    val userEmail by settingsViewModel.userEmail.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Display user email
        userEmail?.let {
            Text(
                text = it,
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
            )
        }


        // Change Password
        Text(
            text = "Change Password",
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { settingsViewModel.onPasswordChange {
                    Toast.makeText(context, "Password reset email sent", Toast.LENGTH_SHORT).show()
                } }
                .padding(vertical = 16.dp)
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = DividerDefaults.Thickness,
            color = Color.White
        )

        // Notifications toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Notifications", fontSize = 18.sp , color = Color.White)
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

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = DividerDefaults.Thickness,
            color = Color.White
        )

        // Logout
        Button(
            onClick = {
                settingsViewModel.logout()
                navController.navigate("login") {
                    popUpTo(Screen.Home.route) {
                        inclusive = true
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Logout", fontSize = 18.sp)
        }
    }
}
