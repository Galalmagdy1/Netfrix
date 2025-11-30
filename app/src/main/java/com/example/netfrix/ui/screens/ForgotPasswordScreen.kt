package com.example.netfrix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.netfrix.R
import com.example.netfrix.ui.theme.darkBlue
import com.example.netfrix.ui.theme.purpleBlue
import com.example.netfrix.viewmodel.AuthViewModel
import com.example.netfrix.viewmodel.AuthState

@Composable
fun ForgotPasswordScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(purpleBlue, darkBlue)
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 180.dp, start = 24.dp, end = 24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.forgot_password),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 28.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = stringResource(R.string.email), color = Color.White.copy(alpha = 0.8f)) },
                textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.White.copy(alpha = 0.07f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.07f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { viewModel.resetPassword(email) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text(text = stringResource(R.string.send_reset_link), color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (val state = authState) {
                is AuthState.Loading -> {
                    Text(
                        text = stringResource(R.string.sending_reset_link),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
                is AuthState.Success -> {
                    Text(
                        text = state.message,
                        color = Color(0xFF4CAF50),
                        fontSize = 14.sp
                    )
                }
                is AuthState.Error -> {
                    Text(
                        text = state.message,
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.remembered),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Text(
                    text = stringResource(R.string.login),
                    color = Color(0xFF2196F3),
                    fontSize = 14.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}
