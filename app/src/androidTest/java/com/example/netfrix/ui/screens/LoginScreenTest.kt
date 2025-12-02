package com.example.netfrix.ui.screens
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_verifyComponentsVisible() {
        composeTestRule.setContent {
            LoginScreen(
                onNavigateToSignUp = {},
                onNavigateToForgotPassword = {},
                onNavigateToHome = {}
            )
        }


        composeTestRule.onNodeWithText("Email").assertIsDisplayed()

        composeTestRule.onNodeWithText("Password").assertIsDisplayed()

        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
    }

    @Test
    fun loginScreen_verifyInputAndClick() {
        composeTestRule.setContent {
            LoginScreen(
                onNavigateToSignUp = {},
                onNavigateToForgotPassword = {},
                onNavigateToHome = {}
            )
        }

        composeTestRule.onNodeWithText("Email")
            .performTextInput("galal@must.edu.eg")

        composeTestRule.onNodeWithText("Password")
            .performTextInput("123456")

        composeTestRule.onNodeWithText("Login").performClick()
    }
}