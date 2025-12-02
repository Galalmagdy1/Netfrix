package com.example.netfrix.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.netfrix.R
import com.example.netfrix.viewmodel.AuthViewModel
import com.example.netfrix.viewmodel.AuthState
import com.example.netfrix.ui.theme.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
@Composable
fun LoginScreen(
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToHome: () -> Unit
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val viewModel: AuthViewModel = viewModel()
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val titleAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "titleAlpha"
    )
    val titleOffsetY by animateFloatAsState(
        targetValue = if (isVisible) 0f else -30f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "titleOffsetY"
    )

    val emailFieldAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 200, easing = FastOutSlowInEasing),
        label = "emailFieldAlpha"
    )
    val emailFieldOffsetY by animateFloatAsState(
        targetValue = if (isVisible) 0f else -20f,
        animationSpec = tween(durationMillis = 600, delayMillis = 200, easing = FastOutSlowInEasing),
        label = "emailFieldOffsetY"
    )

    val passwordFieldAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 350, easing = FastOutSlowInEasing),
        label = "passwordFieldAlpha"
    )
    val passwordFieldOffsetY by animateFloatAsState(
        targetValue = if (isVisible) 0f else -20f,
        animationSpec = tween(durationMillis = 600, delayMillis = 350, easing = FastOutSlowInEasing),
        label = "passwordFieldOffsetY"
    )

    val forgotPasswordAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 500, delayMillis = 500, easing = FastOutSlowInEasing),
        label = "forgotPasswordAlpha"
    )

    val buttonAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 600, easing = FastOutSlowInEasing),
        label = "buttonAlpha"
    )
    val buttonScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "buttonScale"
    )

    val signUpRowAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 500, delayMillis = 700, easing = FastOutSlowInEasing),
        label = "signUpRowAlpha"
    )

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Error -> {

                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.clearState()
            }
            is AuthState.Success -> {
                Toast.makeText(context, (authState as AuthState.Success).message, Toast.LENGTH_SHORT).show()
                onNavigateToHome()
                viewModel.clearState()
            }
            else -> {}
        }
    }

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
                .padding(top = 150.dp, start = 24.dp, end = 24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.welcome_back),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 40.dp)
                    .graphicsLayer {
                        alpha = titleAlpha
                        translationY = titleOffsetY
                    }
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = stringResource(R.string.email), color = Color.White.copy(alpha = 0.8f)) },
                textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                shape = RoundedCornerShape(30.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.White.copy(alpha = 0.1f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .graphicsLayer {
                        alpha = emailFieldAlpha
                        translationY = emailFieldOffsetY
                    },
                enabled = authState != AuthState.Loading
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password), color = Color.White.copy(alpha = 0.8f)) },
                textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                shape = RoundedCornerShape(30.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.White.copy(alpha = 0.1f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                ),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = stringResource(R.string.toggle_password),
                            tint = Color.White
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .graphicsLayer {
                        alpha = passwordFieldAlpha
                        translationY = passwordFieldOffsetY
                    },
                enabled = authState != AuthState.Loading
            )

            Text(
                text = stringResource(R.string.forgot_password_question),
                color = Color(0xFF2196F3),
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { onNavigateToForgotPassword() }
                    .padding(top = 4.dp, end = 4.dp)
                    .graphicsLayer {
                        alpha = forgotPasswordAlpha
                    }
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    viewModel.login(email, password)
                },
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .graphicsLayer {
                        alpha = buttonAlpha
                        scaleX = buttonScale
                        scaleY = buttonScale
                    },
                enabled = authState != AuthState.Loading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.login),
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = signUpRowAlpha
                    }
            ) {
                Text(
                    text = stringResource(R.string.don_t_have_an_account),
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = stringResource(R.string.sign_up),
                    color = Color(0xFF2196F3),
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        onNavigateToSignUp()
                    }
                )
            }
        }
    }
}