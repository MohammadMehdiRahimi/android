package com.example.ui.main

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
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
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun referenceHomeDashboard_rendersAllRedesignedComponents() {
        composeTestRule.setContent {
            MyApplicationTheme(appTheme = AppTheme.PESARANE) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val navController = rememberNavController()
                    ReferenceHomeDashboard(
                        navController = navController,
                        isGuest = true,
                        onLoginClick = {}
                    )
                }
            }
        }

        // Verify Header
        composeTestRule.onNodeWithText("مهمان شتاب").assertIsDisplayed()

        // Verify Performance Chart Card Title
        composeTestRule.onNodeWithText("نمای کلی").assertIsDisplayed()
        composeTestRule.onNodeWithText("هفته گذشته").assertIsDisplayed()

        // Verify Feature Grid Cards
        composeTestRule.onNodeWithText("برنامه‌ریز هوشمند شتاب").assertIsDisplayed()
        composeTestRule.onNodeWithText("شروع کنید").assertIsDisplayed()
        composeTestRule.onNodeWithText("لیگ‌های رقابتی").assertIsDisplayed()
        composeTestRule.onNodeWithText("گروه‌های مطالعاتی من").assertIsDisplayed()
        composeTestRule.onNodeWithText("مطالعه گروهی و رقابت با دوستان").assertIsDisplayed()
        composeTestRule.onNodeWithText("پرسش از همکلاسی‌ها").assertIsDisplayed()
        composeTestRule.onNodeWithText("پاسخ سریع به سؤالات و رفع اشکال درسی").assertIsDisplayed()
        composeTestRule.onNodeWithText("آزمون‌ساز").assertIsDisplayed()
    }

    @Test
    fun shetabBottomNavigation_rendersAllTabs() {
        composeTestRule.setContent {
            MyApplicationTheme(appTheme = AppTheme.PESARANE) {
                ShetabBottomNavigation(
                    selectedTab = 0,
                    onTabSelected = {}
                )
            }
        }

        composeTestRule.onNodeWithText("خانه").assertIsDisplayed()
        composeTestRule.onNodeWithText("برنامه‌ریزی").assertIsDisplayed()
        composeTestRule.onNodeWithText("تحلیل‌گر").assertIsDisplayed()
        composeTestRule.onNodeWithText("آزمون‌ساز").assertIsDisplayed()
        composeTestRule.onNodeWithText("پروفایل").assertIsDisplayed()
    }
}
