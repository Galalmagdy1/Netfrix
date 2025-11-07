package com.example.netfrix.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.netfrix.ui.ui.screens.ForgotPasswordScreen
import com.example.netfrix.ui.ui.screens.LoginScreen
import com.example.netfrix.ui.ui.screens.MainScreen
import com.example.netfrix.ui.ui.screens.SignUpScreen
import com.example.netfrix.ui.ui.screens.SplashScreen
import com.example.netfrix.ui.ui.screens.details.MovieDetailScreen

@Composable
fun NewGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate("signup") },
                onNavigateToForgotPassword = { navController.navigate("forgot") },
                onNavigateToHome = {
                    navController.navigate("main") {
                        popUpTo("login") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("signup") {
            SignUpScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToLoginAfterSuccess = {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        composable("forgot") {
            ForgotPasswordScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
            )
        }

        composable("main") {
            MainScreen(mainNavController = navController)
        }

        composable(
            route = "detailscreen/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId")
            if (movieId != null) {
                MovieDetailScreen(
                    navController = navController,
                    movieId = movieId
                )
            }
        }
    }
}
