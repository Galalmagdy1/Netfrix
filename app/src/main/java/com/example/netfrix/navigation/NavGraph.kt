package com.example.netfrix.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.netfrix.ui.screens.LoginScreen
import com.example.netfrix.ui.screens.SignUpScreen
import com.example.netfrix.ui.screens.ForgotPasswordScreen

@Composable
fun NewGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate("signup") },
                onNavigateToForgotPassword = { navController.navigate("forgot") }
            )
        }

        composable("signup") {
            SignUpScreen(
                onNavigateToLogin = { navController.navigate("login") }
            )
        }

        composable("forgot") {
            ForgotPasswordScreen(
                onNavigateToLogin = { navController.navigate("login") }
            )
        }
    }
}
