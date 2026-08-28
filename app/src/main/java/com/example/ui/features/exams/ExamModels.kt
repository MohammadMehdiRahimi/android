package com.example.ui.features.exams

import androidx.compose.ui.graphics.Color

enum class ExamType(val title: String) {
    MULTIPLE_CHOICE("تستی"),
    DESCRIPTIVE("تشریحی")
}

enum class FilterType {
    DATE,
    SUBJECT,
    TOPIC
}

data class ExamListItem(
    val id: String,
    val subject: String,
    val topic: String,
    val date: String,
    val dayOfWeek: String,
    val examType: ExamType,
    val score: String,
    val scorePercentage: Int,
    val durationMinutes: Int,
    val questionCount: Int,
    val themeColor: Color
)

data class ExamsUiState(
    val isLoading: Boolean = false,
    val allExams: List<ExamListItem> = emptyList(),
    val filteredExams: List<ExamListItem> = emptyList(),
    val availableDates: List<String> = emptyList(),
    val availableSubjects: List<String> = emptyList(),
    val availableTopics: List<String> = emptyList(),
    val selectedDate: String? = null,
    val selectedSubject: String? = null,
    val selectedTopic: String? = null,
    val activeFilterType: FilterType? = null
)

data class SelectedExamBook(
    val id: String,
    val bookName: String,
    val chapter: String,
    val topics: List<String>,
    val gradientColors: List<Color>
)

data class CatalogTopic(
    val id: String,
    val name: String
)

data class CatalogChapter(
    val id: String,
    val name: String,
    val topics: List<CatalogTopic>
)

data class CatalogBook(
    val id: String,
    val name: String,
    val gradientColors: List<Color>,
    val chapters: List<CatalogChapter>
)
