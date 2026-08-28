package com.example.ui.features.exams

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MockExamData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExamsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ExamsUiState(isLoading = false))
    val uiState: StateFlow<ExamsUiState> = _uiState.asStateFlow()

    init {
        loadExams(delayMs = 0)
    }

    fun loadExams(delayMs: Long = 0) {
        viewModelScope.launch {
            if (delayMs > 0) {
                _uiState.update { it.copy(isLoading = true) }
                kotlinx.coroutines.delay(delayMs)
            }

            // Preset mock data structured strictly to match the design prototype
            val defaultExams = listOf(
                ExamListItem(
                    id = "1",
                    subject = "ریاضی دهم",
                    topic = "معادله و نامعادله",
                    date = "۱۴۰۳/۰۳/۲۵",
                    dayOfWeek = "جمعه",
                    examType = ExamType.MULTIPLE_CHOICE,
                    score = "۱۸/۳۰",
                    scorePercentage = 60,
                    durationMinutes = 45,
                    questionCount = 30,
                    themeColor = Color(0xFF7C3AED) // Purple
                ),
                ExamListItem(
                    id = "2",
                    subject = "فیزیک دهم",
                    topic = "فشار و آثار آن",
                    date = "۱۴۰۳/۰۳/۲۲",
                    dayOfWeek = "دوشنبه",
                    examType = ExamType.DESCRIPTIVE,
                    score = "۲۰/۲۵",
                    scorePercentage = 80,
                    durationMinutes = 40,
                    questionCount = 25,
                    themeColor = Color(0xFF10B981) // Green
                ),
                ExamListItem(
                    id = "3",
                    subject = "شیمی دهم",
                    topic = "ساختار اتم",
                    date = "۱۴۰۳/۰۳/۲۰",
                    dayOfWeek = "شنبه",
                    examType = ExamType.MULTIPLE_CHOICE,
                    score = "۱۵/۲۰",
                    scorePercentage = 75,
                    durationMinutes = 30,
                    questionCount = 20,
                    themeColor = Color(0xFFF59E0B) // Orange
                ),
                ExamListItem(
                    id = "4",
                    subject = "زیست دهم",
                    topic = "گوارش و جذب مواد",
                    date = "۱۴۰۳/۰۳/۱۸",
                    dayOfWeek = "پنجشنبه",
                    examType = ExamType.DESCRIPTIVE,
                    score = "۲۱/۲۵",
                    scorePercentage = 84,
                    durationMinutes = 35,
                    questionCount = 25,
                    themeColor = Color(0xFF0EA5E9) // Blue
                ),
                ExamListItem(
                    id = "5",
                    subject = "ریاضی دهم",
                    topic = "توابع و نمودارها",
                    date = "۱۴۰۳/۰۳/۱۵",
                    dayOfWeek = "دوشنبه",
                    examType = ExamType.MULTIPLE_CHOICE,
                    score = "۱۷/۳۰",
                    scorePercentage = 57,
                    durationMinutes = 45,
                    questionCount = 30,
                    themeColor = Color(0xFF7C3AED)
                ),
                ExamListItem(
                    id = "6",
                    subject = "فیزیک دهم",
                    topic = "کار و انرژی",
                    date = "۱۴۰۳/۰۳/۱۰",
                    dayOfWeek = "پنجشنبه",
                    examType = ExamType.MULTIPLE_CHOICE,
                    score = "۲۴/۲۵",
                    scorePercentage = 96,
                    durationMinutes = 30,
                    questionCount = 25,
                    themeColor = Color(0xFF10B981)
                ),
                ExamListItem(
                    id = "7",
                    subject = "شیمی دهم",
                    topic = "پیوندهای شیمیایی",
                    date = "۱۴۰۳/۰۳/۰۵",
                    dayOfWeek = "شنبه",
                    examType = ExamType.DESCRIPTIVE,
                    score = "۱۶/۲۰",
                    scorePercentage = 80,
                    durationMinutes = 40,
                    questionCount = 20,
                    themeColor = Color(0xFFF59E0B)
                ),
                ExamListItem(
                    id = "8",
                    subject = "زیست دهم",
                    topic = "گردش مواد در بدن",
                    date = "۱۴۰۳/۰۲/۲۸",
                    dayOfWeek = "جمعه",
                    examType = ExamType.MULTIPLE_CHOICE,
                    score = "۲۳/۲۵",
                    scorePercentage = 92,
                    durationMinutes = 35,
                    questionCount = 25,
                    themeColor = Color(0xFF0EA5E9)
                ),
                ExamListItem(
                    id = "9",
                    subject = "ادبیات فارسی",
                    topic = "آرایه‌های ادبی و قرابت",
                    date = "۱۴۰۳/۰۲/۲۰",
                    dayOfWeek = "پنجشنبه",
                    examType = ExamType.MULTIPLE_CHOICE,
                    score = "۱۸/۲۰",
                    scorePercentage = 90,
                    durationMinutes = 20,
                    questionCount = 20,
                    themeColor = Color(0xFF7C3AED)
                ),
                ExamListItem(
                    id = "10",
                    subject = "عربی دهم",
                    topic = "ترجمه و قواعد ثلاثی",
                    date = "۱۴۰۳/۰۲/۱۵",
                    dayOfWeek = "شنبه",
                    examType = ExamType.DESCRIPTIVE,
                    score = "۱۹/۲۰",
                    scorePercentage = 95,
                    durationMinutes = 25,
                    questionCount = 20,
                    themeColor = Color(0xFF10B981)
                ),
                ExamListItem(
                    id = "11",
                    subject = "زبان انگلیسی",
                    topic = "گرامر زمان‌ها و واژگان",
                    date = "۱۴۰۳/۰۲/۱۰",
                    dayOfWeek = "دوشنبه",
                    examType = ExamType.MULTIPLE_CHOICE,
                    score = "۲۸/۳۰",
                    scorePercentage = 93,
                    durationMinutes = 30,
                    questionCount = 30,
                    themeColor = Color(0xFF0EA5E9)
                ),
                ExamListItem(
                    id = "12",
                    subject = "دین و زندگی",
                    topic = "هدف آفرینش و توحید",
                    date = "۱۴۰۳/۰۲/۰۲",
                    dayOfWeek = "یکشنبه",
                    examType = ExamType.DESCRIPTIVE,
                    score = "۲۰/۲۰",
                    scorePercentage = 100,
                    durationMinutes = 25,
                    questionCount = 20,
                    themeColor = Color(0xFFF59E0B)
                )
            )

            val availableDates = defaultExams.map { it.date }.distinct()
            val availableSubjects = defaultExams.map { it.subject }.distinct()
            val availableTopics = defaultExams.map { it.topic }.distinct()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    allExams = defaultExams,
                    filteredExams = defaultExams,
                    availableDates = availableDates,
                    availableSubjects = availableSubjects,
                    availableTopics = availableTopics
                )
            }
        }
    }

    fun openFilterSheet(filterType: FilterType) {
        _uiState.update { it.copy(activeFilterType = filterType) }
    }

    fun dismissFilterSheet() {
        _uiState.update { it.copy(activeFilterType = null) }
    }

    fun selectDate(date: String?) {
        _uiState.update { state ->
            val newState = state.copy(selectedDate = date, activeFilterType = null)
            newState.copy(filteredExams = applyFilters(newState))
        }
    }

    fun selectSubject(subject: String?) {
        _uiState.update { state ->
            val newState = state.copy(
                selectedSubject = subject,
                // Reset topic if selected topic does not belong to new subject
                selectedTopic = if (subject != null && state.selectedTopic != null) {
                    val matching = state.allExams.any { it.subject == subject && it.topic == state.selectedTopic }
                    if (matching) state.selectedTopic else null
                } else state.selectedTopic,
                activeFilterType = null
            )
            newState.copy(filteredExams = applyFilters(newState))
        }
    }

    fun selectTopic(topic: String?) {
        _uiState.update { state ->
            val newState = state.copy(selectedTopic = topic, activeFilterType = null)
            newState.copy(filteredExams = applyFilters(newState))
        }
    }

    fun clearAllFilters() {
        _uiState.update { state ->
            val newState = state.copy(
                selectedDate = null,
                selectedSubject = null,
                selectedTopic = null,
                activeFilterType = null
            )
            newState.copy(filteredExams = state.allExams)
        }
    }

    private fun applyFilters(state: ExamsUiState): List<ExamListItem> {
        return state.allExams.filter { exam ->
            val matchDate = state.selectedDate == null || exam.date == state.selectedDate
            val matchSubject = state.selectedSubject == null || exam.subject == state.selectedSubject
            val matchTopic = state.selectedTopic == null || exam.topic == state.selectedTopic
            matchDate && matchSubject && matchTopic
        }
    }
}
