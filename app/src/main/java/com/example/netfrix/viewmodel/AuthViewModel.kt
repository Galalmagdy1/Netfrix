package com.example.netfrix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.netfrix.R
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
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
    fun signout() {
        auth.signOut()
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please fill in all fields.")
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
                            _authState.value = AuthState.Error("Please verify your email first.")
                            auth.signOut()
                        }
                    } else {

                        _authState.value = AuthState.Error(getFriendlyErrorMessage(task.exception))
                    }
                }
        }
    }

    fun signUp(email: String, password: String, confirmPass: String) {
        if (email.isBlank() || password.isBlank() || confirmPass.isBlank()) {
            _authState.value = AuthState.Error("Please fill in all fields.")
            return
        } else if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters.")
            return
        } else if (password != confirmPass) {
            _authState.value = AuthState.Error("Passwords do not match.")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        auth.currentUser?.sendEmailVerification()
                            ?.addOnSuccessListener {
                                _authState.value = AuthState.Success("Account created! Check your email.")
                            }
                            ?.addOnFailureListener {
                                _authState.value = AuthState.Error("Failed to send verification email.")
                            }
                    } else {

                        _authState.value = AuthState.Error(getFriendlyErrorMessage(task.exception))
                    }
                }
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _authState.value = AuthState.Error("Please enter your email.")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _authState.value = AuthState.Success("Reset link sent to your email!")
                    } else {
                        _authState.value = AuthState.Error(getFriendlyErrorMessage(task.exception))
                    }
                }
        }
    }

    fun clearState() {
        _authState.value = AuthState.Idle
    }


    private fun getFriendlyErrorMessage(exception: Exception?): String {
        return when (exception) {
            is FirebaseAuthInvalidUserException -> "Account not found. Please sign up."

            is FirebaseAuthInvalidCredentialsException -> "Invalid email or password."

            is FirebaseAuthUserCollisionException -> "This email is already in use."

            is FirebaseNetworkException -> "Network error. Check your connection."

            else -> "An error occurred. Please try again."
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}