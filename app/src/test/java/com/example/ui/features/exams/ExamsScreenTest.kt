package com.example.ui.features.exams

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
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

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class ExamsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun examsScreen_rendersHeader_filterChips_and_examItems() {
        composeTestRule.setContent {
            MyApplicationTheme(appTheme = AppTheme.PESARANE) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val navController = rememberNavController()
                    ExamsScreen(navController = navController)
                }
            }
        }

        // Verify Top Bar Header
        composeTestRule.onNodeWithText("آزمون‌های من").assertIsDisplayed()
        composeTestRule.onNodeWithTag("notification_button").assertIsDisplayed()

        // Verify Filter Chips
        composeTestRule.onNodeWithText("همه تاریخ‌ها").assertIsDisplayed()
        composeTestRule.onNodeWithText("همه درس‌ها").assertIsDisplayed()
        composeTestRule.onNodeWithText("همه مباحث").assertIsDisplayed()

        // Verify Count
        composeTestRule.onNodeWithText("تعداد کل: ").assertIsDisplayed()
        composeTestRule.onNodeWithTag("total_exams_count").assertIsDisplayed()

        // Verify First Exam Card Elements
        composeTestRule.onAllNodesWithText("ریاضی دهم").onFirst().assertIsDisplayed()
        composeTestRule.onNodeWithText("معادله و نامعادله").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("تستی").onFirst().assertIsDisplayed()

        // Verify FAB
        composeTestRule.onNodeWithTag("create_exam_fab").assertIsDisplayed()
    }

    @Test
    fun filterChip_clickingSubject_opensBottomSheet() {
        composeTestRule.setContent {
            MyApplicationTheme(appTheme = AppTheme.PESARANE) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val navController = rememberNavController()
                    ExamsScreen(navController = navController)
                }
            }
        }

        // Click Subject Filter Chip
        composeTestRule.onNodeWithTag("filter_chip_subject").performClick()

        // Verify bottom sheet title is displayed
        composeTestRule.onNodeWithText("فیلتر بر اساس درس").assertIsDisplayed()
    }
}
