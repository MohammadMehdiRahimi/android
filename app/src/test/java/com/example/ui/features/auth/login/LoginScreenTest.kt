package com.example.ui.features.auth.login

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.compose.rememberNavController
import com.example.ui.theme.AppTheme
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

import com.example.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockRepo = object : com.example.data.repository.AuthRepository {
        override fun sendOtp(phone: String): Flow<NetworkResult<Unit>> = flow {}
        override fun verifyOtp(phone: String, code: String): Flow<NetworkResult<com.example.network.OtpVerifyResponseDto>> = flow {}
        override fun register(
            phone: String,
            registrationToken: String,
            fullName: String,
            grade: String,
            fieldOfStudy: String?,
            deviceType: String
        ): Flow<NetworkResult<com.example.network.AuthResponseDto>> = flow {}
        override fun logout(): Flow<NetworkResult<Unit>> = flow {}
    }

    @Test
    fun loginScreen_elementsAreDisplayed() {
        composeTestRule.setContent {
            MyApplicationTheme(appTheme = AppTheme.PESARANE) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val navController = rememberNavController()
                    LoginScreen(navController = navController, viewModel = LoginViewModel(mockRepo))
                }
            }
        }

        // Verify key UI nodes are displayed with proper test tags
        composeTestRule.onNodeWithTag("login_hero_image").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_title").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_subtitle").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_phone_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_submit_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_terms_text").assertIsDisplayed()
    }

    @Test
    fun loginScreen_inputPhone_updatesState() {
        val viewModel = LoginViewModel(mockRepo)

        composeTestRule.setContent {
            MyApplicationTheme(appTheme = AppTheme.PESARANE) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val navController = rememberNavController()
                    LoginScreen(navController = navController, viewModel = viewModel)
                }
            }
        }

        composeTestRule.onNodeWithTag("login_phone_input").performTextInput("09123456789")
        assert(viewModel.uiState.value.rawPhoneNumber == "09123456789")
        assert(viewModel.uiState.value.isValid)
    }
}
