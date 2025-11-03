package com.example.netfrix.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.netfrix.viewmodel.AuthViewModel

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val DarkBlue = Color(0xFF0D0C1D)
    val PurpleBlue = Color(0xFFB74F7B)

    // ده بيشتغل أول ما الشاشة تفتح
    LaunchedEffect(key1 = true) {
        // هنا بنستخدم الدالة الجديدة
        if (viewModel.getCurrentVerifiedUser()) {
            // لو مسجل وعمل verify -> روح للهوم
            onNavigateToHome()
        } else {
            // لو مش مسجل أو مسجل بس معملش verify -> روح للوجن
            onNavigateToLogin()
        }
    }

    // مجرد شاشة تحميل شكلية
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PurpleBlue, DarkBlue)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}