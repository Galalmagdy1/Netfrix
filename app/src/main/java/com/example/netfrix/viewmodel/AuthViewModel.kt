package com.example.netfrix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState


    fun getCurrentVerifiedUser(): Boolean {
        val user = auth.currentUser
        return user != null && user.isEmailVerified
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Missing field")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser

                        if (user != null && user.isEmailVerified) {
                            _authState.value = AuthState.Success("Login successful!")
                        } else {

                            _authState.value = AuthState.Error("Please verify your email before logging in.")
                            auth.signOut()
                        }
                    } else {
                        _authState.value =
                            AuthState.Error(task.exception?.message ?: "Login failed")
                    }
                }
        }
    }

    fun signUp(email: String, password: String, confirmPass: String) {
        if (email.isBlank() || password.isBlank() || confirmPass.isBlank()) {
            _authState.value = AuthState.Error("Missing field")
            return
        } else if (password.length < 6) {
            _authState.value = AuthState.Error("Short password")
            return
        } else if (password != confirmPass) {
            _authState.value = AuthState.Error("Passwords don't match")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        auth.currentUser?.sendEmailVerification()
                            ?.addOnSuccessListener {
                                _authState.value = AuthState.Success("Account created! Please check your email for verification.")
                            }
                            ?.addOnFailureListener {
                                _authState.value = AuthState.Error(it.message ?: "Failed to send verification email.")
                            }
                    } else {
                        _authState.value =
                            AuthState.Error(task.exception?.message ?: "Signup failed")
                    }
                }
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _authState.value = AuthState.Error("Please enter your email")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _authState.value = AuthState.Success("Reset link sent!")
                    } else {
                        _authState.value =
                            AuthState.Error(task.exception?.message ?: "Failed to send link")
                    }
                }
        }
    }

    fun clearState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}