package com.example.netfrix.ui.ui.screens.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs: SharedPreferences = context.getSharedPreferences("netfrix_prefs", Context.MODE_PRIVATE)
    private val NOTIFICATIONS_ENABLED_KEY = "notifications_enabled"

    // Dark Mode
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // Notifications - load from SharedPreferences on init
    private val _notificationsEnabled = MutableStateFlow(
        prefs.getBoolean(NOTIFICATIONS_ENABLED_KEY, true) // default to true
    )
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    fun toggleNotifications() {
        val newValue = !_notificationsEnabled.value
        _notificationsEnabled.value = newValue
        prefs.edit().putBoolean(NOTIFICATIONS_ENABLED_KEY, newValue).apply()
    }
    
    // Helper function to check if notifications are enabled (for use in other classes)
    fun areNotificationsEnabled(): Boolean {
        return prefs.getBoolean(NOTIFICATIONS_ENABLED_KEY, true)
    }
}
