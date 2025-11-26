package com.example.netfrix.ui.ui.screens.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val prefs: SharedPreferences = context.getSharedPreferences("netfrix_prefs", Context.MODE_PRIVATE)
    private val NOTIFICATIONS_ENABLED_KEY = "notifications_enabled"

    // User Email
    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()

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

    init {
        _userEmail.value = auth.currentUser?.email
    }

    fun toggleNotifications() {
        val newValue = !_notificationsEnabled.value
        _notificationsEnabled.value = newValue
        prefs.edit().putBoolean(NOTIFICATIONS_ENABLED_KEY, newValue).apply()
    }

    fun onLogout(
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            auth.signOut()
            onSuccess()
        }
    }

    fun onPasswordChange(
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            auth.sendPasswordResetEmail(auth.currentUser?.email!!)
            onSuccess()
        }
    }

    fun logout() {
        auth.signOut()
    }
}
