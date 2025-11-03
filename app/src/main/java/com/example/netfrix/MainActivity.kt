package com.example.netfrix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.netfrix.navigation.NewGraph
import com.example.netfrix.ui.screens.ForgotPasswordScreen
import com.example.netfrix.ui.screens.LoginScreen
import com.example.netfrix.ui.screens.SignUpScreen
import com.example.netfrix.ui.theme.NetfrixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NetfrixTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NewGraph()
                }
            }
        }
    }
}