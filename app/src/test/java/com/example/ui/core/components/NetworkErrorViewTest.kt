package com.example.ui.core.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NetworkErrorViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `NetworkErrorView renders title and description and handles retry click`() {
        var retryClicked = false

        composeTestRule.setContent {
            NetworkErrorView(
                title = "عدم برقراری ارتباط با سرور",
                description = "لطفاً اتصال اینترنت خود را بررسی نمایید.",
                retryButtonText = "تلاش مجدد",
                isRetrying = false,
                fullScreen = true,
                onRetry = { retryClicked = true }
            )
        }

        composeTestRule.onNodeWithTag("network_error_view").assertIsDisplayed()
        composeTestRule.onNodeWithText("عدم برقراری ارتباط با سرور").assertIsDisplayed()
        composeTestRule.onNodeWithText("لطفاً اتصال اینترنت خود را بررسی نمایید.").assertIsDisplayed()
        composeTestRule.onNodeWithTag("network_retry_button").assertIsDisplayed()

        composeTestRule.onNodeWithTag("network_retry_button").performClick()
        assertTrue("onRetry callback should have been invoked", retryClicked)
    }

    @Test
    fun `NetworkErrorView shows retrying loading state when isRetrying is true`() {
        var retryCount = 0

        composeTestRule.setContent {
            NetworkErrorView(
                title = "خطای اتصال",
                description = "مشکل در اینترنت",
                isRetrying = true,
                fullScreen = false,
                onRetry = { retryCount++ }
            )
        }

        composeTestRule.onNodeWithTag("network_error_view").assertIsDisplayed()
        composeTestRule.onNodeWithText("در حال اتصال...").assertIsDisplayed()
        composeTestRule.onNodeWithTag("network_retry_button").assertIsNotEnabled()

        composeTestRule.onNodeWithTag("network_retry_button").performClick()
        assertEquals("onRetry should not be called when isRetrying is true", 0, retryCount)
    }
}
