package com.example.netfrix.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.netfrix.ui.screens.ForgotPasswordScreen
import com.example.netfrix.ui.screens.LoginScreen
import com.example.netfrix.ui.screens.MainScreen
import com.example.netfrix.ui.screens.SignUpScreen
import com.example.netfrix.ui.screens.SplashScreen
import com.example.netfrix.ui.screens.DetailScreen
import com.example.netfrix.ui.screens.SettingsScreen
import com.example.netfrix.ui.screens.Screen
import com.example.netfrix.viewmodel.SettingsViewModel

@Composable
fun NewGraph(
    settingsViewModel: SettingsViewModel,
    openFavorites: Boolean,
    onFavoritesHandled: () -> Unit
) {
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
            MainScreen(
                mainNavController = navController,
                settingsViewModel = settingsViewModel,
                openFavorites = openFavorites,
                onFavoritesHandled = onFavoritesHandled
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }


        composable(
            route = "detailscreen/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId")
            if (movieId != null) {
                DetailScreen(
                    navController = navController,
                    movieId = movieId
                )
            }
        }
    }
}
