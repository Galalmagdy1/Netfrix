package com.example.netfrix.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.netfrix.ui.screens.LoginScreen
import com.example.netfrix.ui.screens.SignUpScreen
import com.example.netfrix.ui.screens.ForgotPasswordScreen
import com.example.netfrix.ui.screens.HomeScreen
import com.example.netfrix.ui.screens.SplashScreen

@Composable
fun NewGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash" //  التعديل: ابدأ من شاشة التحميل
    ) {

        // 1. الشاشة الجديدة (شاشة البداية)
        composable("splash") {
            SplashScreen(
                onNavigateToHome = {
                    // امسح الـ Splash وروح للهوم
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    // امسح الـ Splash وروح للوجن
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // 2. شاشة اللوجن (بالتعديل الصح)
        composable("login") {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate("signup") },
                onNavigateToForgotPassword = { navController.navigate("forgot") },

                onNavigateToHome = {
                    navController.navigate("home") {
                        //  التعديل الصح اهو
                        popUpTo("login") { //  <-- غير "splash" خليها "login"
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // 3. شاشة التسجيل
        composable("signup") {
            SignUpScreen(
                onNavigateToLogin = {
                    // لو داس على "Login" من تحت (يرجع شاشة واحدة)
                    navController.popBackStack()
                },
                onNavigateToLoginAfterSuccess = {
                    //  التعديل: بعد نجاح التسجيل، ارجع للـ Login وامسح الـ Signup
                    navController.navigate("login") {
                        // امسح بس شاشة الـ "signup"
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        // 4. شاشة نسيت الباسورد
        composable("forgot") {
            ForgotPasswordScreen(
                onNavigateToLogin = {
                    //  التعديل: ارجع شاشة واحدة (اللي هي Login)
                    navController.popBackStack()
                },
            )
        }

        // 5. شاشة الهوم
        composable("home") {
            HomeScreen()
            // ملحوظة: لو عايز تضيف زرار Logout في الـ HomeScreen
            // خليه يعمل:
            // viewModel.logout()  (هتحتاج تعمل دالة logout في الفيو مودل)
            // navController.navigate("login") {
            //    popUpTo("home") { inclusive = true }
            // }
        }
    }

}