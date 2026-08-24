package com.example.ui.features.auth.otp

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.compose.rememberNavController
import com.example.ui.theme.AppTheme
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
class VerifyOtpScreenTest {

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
    fun verifyOtpScreen_elementsAreDisplayed() {
        val viewModel = VerifyOtpViewModel("09123456789", mockRepo)

        composeTestRule.setContent {
            MyApplicationTheme(appTheme = AppTheme.PESARANE) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val navController = rememberNavController()
                    VerifyOtpScreen(
                        navController = navController,
                        phoneNumber = "09123456789",
                        viewModel = viewModel
                    )
                }
            }
        }

        // Verify all key visual nodes and test tags exist in the hierarchy
        composeTestRule.onNodeWithTag("otp_back_button").assertExists()
        composeTestRule.onNodeWithTag("otp_hero_image").assertExists()
        composeTestRule.onNodeWithTag("otp_title").assertExists()
        composeTestRule.onNodeWithTag("otp_subtitle").assertExists()
        composeTestRule.onNodeWithTag("otp_input_container").assertExists()
        composeTestRule.onNodeWithTag("otp_timer_row").assertExists()
        composeTestRule.onNodeWithTag("otp_resend_button").assertExists()
        composeTestRule.onNodeWithTag("otp_submit_button").assertExists()

        // Verify individual 6 OTP boxes
        for (i in 0..5) {
            composeTestRule.onNodeWithTag("otp_box_$i", useUnmergedTree = true).assertExists()
        }
    }

    @Test
    fun verifyOtpScreen_typingOtp_updatesOtpBoxesAndState() {
        val viewModel = VerifyOtpViewModel("09123456789", mockRepo)

        composeTestRule.setContent {
            MyApplicationTheme(appTheme = AppTheme.PESARANE) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val navController = rememberNavController()
                    VerifyOtpScreen(
                        navController = navController,
                        phoneNumber = "09123456789",
                        viewModel = viewModel
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag("otp_hidden_text_field").performTextInput("123456")

        assertEquals("123456", viewModel.uiState.value.otpCode)
        assertTrue(viewModel.uiState.value.isCodeComplete)
    }
}
