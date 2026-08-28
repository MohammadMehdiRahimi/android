package com.example

import com.example.ui.features.exams.BookQuestionConfig
import com.example.ui.features.exams.ExamListItem
import com.example.ui.features.exams.ExamType
import com.example.ui.features.exams.ExamsUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExamsFeatureUnitTest {

    @Test
    fun testBookQuestionConfig_totalCountCalculation() {
        val config = BookQuestionConfig(
            bookId = "math_10",
            bookName = "ریاضی دهم",
            chapter = "فصل ۲",
            topics = listOf("تابع"),
            gradientColors = listOf(androidx.compose.ui.graphics.Color(0xFF6366F1), androidx.compose.ui.graphics.Color(0xFF4338CA)),
            easyCount = 4,
            mediumCount = 5,
            hardCount = 3,
            veryHardCount = 2
        )
        assertEquals(14, config.totalCount)
    }

    @Test
    fun testExamType_titles() {
        assertEquals("تستی", ExamType.MULTIPLE_CHOICE.title)
        assertEquals("تشریحی", ExamType.DESCRIPTIVE.title)
    }

    @Test
    fun testExamsUiState_loadingAndFiltering() {
        val initialState = ExamsUiState(isLoading = true)
        assertTrue(initialState.isLoading)
        assertTrue(initialState.allExams.isEmpty())

        val loadedState = initialState.copy(
            isLoading = false,
            allExams = listOf(
                ExamListItem(
                    id = "1",
                    subject = "ریاضی دهم",
                    topic = "تابع",
                    date = "۱۴۰۳/۰۴/۱۵",
                    dayOfWeek = "جمعه",
                    examType = ExamType.MULTIPLE_CHOICE,
                    score = "۱۸/۲۰",
                    scorePercentage = 90,
                    durationMinutes = 45,
                    questionCount = 20,
                    themeColor = androidx.compose.ui.graphics.Color(0xFF7C3AED)
                )
            )
        )
        assertFalse(loadedState.isLoading)
        assertEquals(1, loadedState.allExams.size)
    }
}
