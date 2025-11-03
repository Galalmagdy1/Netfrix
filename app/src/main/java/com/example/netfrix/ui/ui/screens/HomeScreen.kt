package com.example.netfrix.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0C1D)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Home Screen",
            color = Color.White,
            fontSize = 28.sp
        )
    }
}
