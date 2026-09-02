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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class ExamDetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun examDetailsViewModel_loadsExamDetailsSuccessfully() {
        val viewModel = ExamDetailsViewModel()
        viewModel.loadExamDetails("2841")

        val state = viewModel.uiState.value
        assertNotNull(state.examDetails)
        assertEquals("2841", state.examDetails?.examId)
        assertEquals("استاد احمدی", state.examDetails?.organizer)
    }

    @Test
    fun examDetailsScreen_rendersAllDetailsAndEnterButton() {
        composeTestRule.setContent {
            MyApplicationTheme(appTheme = AppTheme.PESARANE) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val navController = rememberNavController()
                    ExamDetailsScreen(
                        navController = navController,
                        examId = "2841"
                    )
                }
            }
        }

        // Verify Screen & Header
        composeTestRule.onNodeWithTag("exam_details_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("exam_details_back_button").assertIsDisplayed()

        // Verify ID Card & Hero Card
        composeTestRule.onNodeWithTag("exam_id_input_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("exam_details_hero_card").assertIsDisplayed()

        // Verify Specifications text
        composeTestRule.onNodeWithText("برگزارکننده").assertIsDisplayed()
        composeTestRule.onNodeWithText("استاد احمدی").assertIsDisplayed()
        composeTestRule.onNodeWithText("تاریخ برگزاری").assertIsDisplayed()
        composeTestRule.onNodeWithText("جمعه ۲۴ خرداد ۱۴۰۳").assertIsDisplayed()

        // Verify Enter Exam button
        composeTestRule.onNodeWithTag("enter_exam_button").assertIsDisplayed()
    }

    @Test
    fun examsScreen_rendersExamIdSearchBox() {
        composeTestRule.setContent {
            MyApplicationTheme(appTheme = AppTheme.PESARANE) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val navController = rememberNavController()
                    ExamsScreen(navController = navController)
                }
            }
        }

        // Verify Search Card & Elements
        composeTestRule.onNodeWithTag("exam_id_search_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("exam_id_search_input").assertIsDisplayed()
        composeTestRule.onNodeWithTag("submit_exam_id_button").assertIsDisplayed()
    }
}
