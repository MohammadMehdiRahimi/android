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
class BuildExamWizardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun buildExamScreen_rendersStep1_elementsCorrectly() {
        composeTestRule.setContent {
            MyApplicationTheme(appTheme = AppTheme.PESARANE) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val navController = rememberNavController()
                    BuildExamScreen(navController = navController)
                }
            }
        }

        // 1. Verify Top App Bar & Stepper
        composeTestRule.onNodeWithText("طراحی آزمون جدید").assertExists()
        composeTestRule.onNodeWithText("مرحله ۱: انتخاب ساختار آزمون").assertExists()
        composeTestRule.onNodeWithText("۱. ساختار آزمون").assertExists()
        composeTestRule.onNodeWithText("۲. تنظیم سوالات").assertExists()
        composeTestRule.onNodeWithText("۳. ساخت آزمون").assertExists()

        // 2. Verify Exam Type Selection
        composeTestRule.onAllNodesWithText("نوع آزمون").onFirst().assertExists()
        composeTestRule.onNodeWithTag("exam_type_multiple_choice").assertExists()
        composeTestRule.onNodeWithTag("exam_type_descriptive").assertExists()

        // 3. Verify Grade & Major Dropdowns
        composeTestRule.onNodeWithText("پایه و رشته").assertExists()
        composeTestRule.onNodeWithTag("grade_dropdown").assertExists()
        composeTestRule.onNodeWithTag("field_dropdown").assertExists()

        // 4. Verify Books & Scope Section
        composeTestRule.onNodeWithText("کتاب‌ها و محدوده آزمون").assertExists()
        composeTestRule.onNodeWithTag("add_book_button").assertExists()

        // 5. Verify Question Source Section
        composeTestRule.onNodeWithText("منبع سوالات").assertExists()
        composeTestRule.onNodeWithTag("source_author").assertExists()
        composeTestRule.onNodeWithTag("source_konkur").assertExists()
        composeTestRule.onNodeWithTag("source_final").assertExists()

        // 6. Verify Navigation Buttons
        composeTestRule.onNodeWithTag("step1_next_button").assertExists()
        composeTestRule.onNodeWithTag("step1_cancel_button").assertExists()
    }

    @Test
    fun buildExamScreen_opensInlineAddBook_andAddsBook() {
        composeTestRule.setContent {
            MyApplicationTheme(appTheme = AppTheme.PESARANE) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val navController = rememberNavController()
                    BuildExamScreen(navController = navController)
                }
            }
        }

        // Open inline add book box
        composeTestRule.onNodeWithTag("add_book_button").performScrollTo().performClick()

        // Check inline box content
        composeTestRule.onNodeWithText("انتخاب کتاب، فصل و مبحث").assertExists()
        composeTestRule.onNodeWithTag("confirm_add_book_button").assertExists()
    }

    @Test
    fun buildExamScreen_rendersStep2_matchingReferenceDesign() {
        composeTestRule.setContent {
            MyApplicationTheme(appTheme = AppTheme.PESARANE) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val navController = rememberNavController()
                    BuildExamScreen(navController = navController)
                }
            }
        }

        // Navigate from Step 1 to Step 2
        composeTestRule.onNodeWithTag("step1_next_button").performScrollTo().performClick()

        // Verify Step 2 header & stepper
        composeTestRule.onNodeWithText("مرحله ۲: تنظیم سوالات").assertExists()

        // Verify Step 2 summary card (2 rows)
        composeTestRule.onAllNodesWithText("آزمون تستی").onFirst().assertExists()
        composeTestRule.onNodeWithText("دهم").assertExists()
        composeTestRule.onNodeWithText("ریاضی‌فیزیک").assertExists()
        composeTestRule.onNodeWithText("تعداد کتاب: ۲").assertExists()
        composeTestRule.onNodeWithText("منبع: تألیفی / کنکور").assertExists()

        // Verify Book question cards
        composeTestRule.onNodeWithText("ریاضی دهم").assertExists()
        composeTestRule.onNodeWithText("فصل ۲: تابع").assertExists()
        composeTestRule.onAllNodesWithText("موضوعات انتخابی:").onFirst().assertExists()
        composeTestRule.onAllNodesWithText("آسان").onFirst().assertExists()
        composeTestRule.onAllNodesWithText("متوسط").onFirst().assertExists()
        composeTestRule.onAllNodesWithText("دشوار").onFirst().assertExists()
        composeTestRule.onAllNodesWithText("خیلی دشوار").onFirst().assertExists()

        // Verify Test Options Card
        composeTestRule.onNodeWithText("تنظیمات آزمون تستی").assertExists()
        composeTestRule.onNodeWithText("نمره منفی").assertExists()
        composeTestRule.onNodeWithText("هر ۴ پاسخ غلط = ۱ پاسخ صحیح منفی").assertExists()
        composeTestRule.onNodeWithText("چینش تصادفی سوالات").assertExists()

        // Verify Navigation buttons
        composeTestRule.onNodeWithTag("step2_next_button").assertExists()
        composeTestRule.onNodeWithTag("step2_prev_button").assertExists()

        // Navigate to Step 3
        composeTestRule.onNodeWithTag("step2_next_button").performScrollTo().performClick()
        composeTestRule.onNodeWithText("مرحله ۳: ساخت آزمون").assertExists()
        composeTestRule.onNodeWithTag("start_exam_button").assertExists()
    }
}
