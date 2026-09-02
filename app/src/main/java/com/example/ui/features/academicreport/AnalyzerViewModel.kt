package com.example.ui.features.academicreport

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AnalyzerViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AnalyzerUiState())
    val uiState: StateFlow<AnalyzerUiState> = _uiState.asStateFlow()

    fun selectTimeframe(timeframe: AnalyzerTimeframe) {
        if (_uiState.value.selectedTimeframe == timeframe) return

        _uiState.update { currentState ->
            when (timeframe) {
                AnalyzerTimeframe.LAST_WEEK -> currentState.copy(
                    selectedTimeframe = timeframe,
                    aiInsightParagraphs = listOf(
                        "عملکرد شما نسبت به هفته قبل ۷.۲٪ بهتر شده است.",
                        "در درس ریاضی و فیزیک پیشرفت خوبی داشته‌اید.",
                        "برای افزایش درصد توصیه‌می‌شود روی مباحث ادبیات تمرکز بیشتری داشته باشید."
                    ),
                    aiInsights = listOf(
                        AiInsightItem(title = "پیشنهاد ما", value = "مرور مباحث ادبیات", type = InsightType.RECOMMENDATION),
                        AiInsightItem(title = "بهترین تمرکز", value = "شب ها (۲۰-۲۴)", type = InsightType.PEAK_FOCUS),
                    ),
                    correctTestsCount = 328,
                    correctTestsSubtitle = "۶۷.۳ درصد موفقیت",
                    totalTestsCount = 486,
                    totalTestsSubtitle = "▲ ۱۵٪ نسبت به هفته قبل",
                    totalExamsCount = 6,
                    totalExamsSubtitle = "۴ آزمون",
                    wrongTestsCount = 158,
                    wrongTestsSubtitle = "▼ ۸٪ نسبت به هفته قبل",
                    weaknesses = listOf(
                        SubjectPerformance(name = "فیزیک", percentage = 31),
                        SubjectPerformance(name = "ادبیات", percentage = 42),
                        SubjectPerformance(name = "عربی", percentage = 45),
                    ),
                    strengths = listOf(
                        SubjectPerformance(name = "زیست شناسی", percentage = 84),
                        SubjectPerformance(name = "شیمی", percentage = 78),
                        SubjectPerformance(name = "ریاضی", percentage = 72),
                    ),
                    studyDistribution = listOf(
                        StudyDistributionPoint(timeSlot = "۰-۴", hours = 0.8f),
                        StudyDistributionPoint(timeSlot = "۴-۸", hours = 1.4f),
                        StudyDistributionPoint(timeSlot = "۸-۱۲", hours = 2.3f),
                        StudyDistributionPoint(timeSlot = "۱۲-۱۶", hours = 3.6f, isPeak = true),
                        StudyDistributionPoint(timeSlot = "۱۶-۲۰", hours = 2.0f),
                        StudyDistributionPoint(timeSlot = "۲۰-۲۴", hours = 0.6f),
                    ),
                    peakStudyHoursBadge = "۳ ساعت"
                )
                AnalyzerTimeframe.LAST_MONTH -> currentState.copy(
                    selectedTimeframe = timeframe,
                    aiInsightParagraphs = listOf(
                        "در ماه گذشته پایداری مطالعه شما به طور چشمگیری افزایش یافته است.",
                        "تعداد تست‌های درس زیست‌شناسی و شیمی به بیشترین حد در این دوره رسیده است.",
                        "پیشنهاد می‌شود زمان حل تست درس فیزیک را در نوبت صبحگاهی افزایش دهید."
                    ),
                    aiInsights = listOf(
                        AiInsightItem(title = "پیشنهاد ما", value = "تست ترکیبی شیمی و زیست", type = InsightType.RECOMMENDATION),
                        AiInsightItem(title = "بهترین تمرکز", value = "عصرها (۱۶-۱۹)", type = InsightType.PEAK_FOCUS),
                    ),
                    correctTestsCount = 1420,
                    correctTestsSubtitle = "۷۲.۱ درصد موفقیت",
                    totalTestsCount = 1960,
                    totalTestsSubtitle = "▲ ۲۲٪ رشد ماهانه",
                    totalExamsCount = 24,
                    totalExamsSubtitle = "۱۸ آزمون جامع",
                    wrongTestsCount = 540,
                    wrongTestsSubtitle = "▼ ۱۲٪ کاهش خطا",
                    weaknesses = listOf(
                        SubjectPerformance(name = "فیزیک", percentage = 38),
                        SubjectPerformance(name = "ادبیات", percentage = 49),
                        SubjectPerformance(name = "عربی", percentage = 52),
                    ),
                    strengths = listOf(
                        SubjectPerformance(name = "زیست شناسی", percentage = 89),
                        SubjectPerformance(name = "شیمی", percentage = 82),
                        SubjectPerformance(name = "ریاضی", percentage = 76),
                    ),
                    studyDistribution = listOf(
                        StudyDistributionPoint(timeSlot = "۰-۴", hours = 0.5f),
                        StudyDistributionPoint(timeSlot = "۴-۸", hours = 1.8f),
                        StudyDistributionPoint(timeSlot = "۸-۱۲", hours = 3.2f),
                        StudyDistributionPoint(timeSlot = "۱۲-۱۶", hours = 3.8f, isPeak = true),
                        StudyDistributionPoint(timeSlot = "۱۶-۲۰", hours = 2.4f),
                        StudyDistributionPoint(timeSlot = "۲۰-۲۴", hours = 1.1f),
                    ),
                    peakStudyHoursBadge = "۳.۸ ساعت"
                )
                AnalyzerTimeframe.LAST_3_MONTHS -> currentState.copy(
                    selectedTimeframe = timeframe,
                    aiInsightParagraphs = listOf(
                        "روند کلی ۳ ماهه شما نشان‌دهنده جهش تراز در آزمون‌های آزمایشی است.",
                        "تداوم و تسلط بالا در دروس تخصصی تجربی / ریاضی تثبیت شده است.",
                        "توصیه اکید: مرور روتین‌های روزانه واژگان و درک مطلب عربی."
                    ),
                    aiInsights = listOf(
                        AiInsightItem(title = "پیشنهاد ما", value = "مرور خلاصه‌نویسی‌ها", type = InsightType.RECOMMENDATION),
                        AiInsightItem(title = "بهترین تمرکز", value = "ظهرها (۱۲-۱۶)", type = InsightType.PEAK_FOCUS),
                    ),
                    correctTestsCount = 4620,
                    correctTestsSubtitle = "۷۵.۸ درصد موفقیت",
                    totalTestsCount = 6100,
                    totalTestsSubtitle = "▲ ۳۵٪ رشد تجمعی",
                    totalExamsCount = 68,
                    totalExamsSubtitle = "۵۴ آزمون هدفمند",
                    wrongTestsCount = 1480,
                    wrongTestsSubtitle = "▼ ۱۹٪ کاهش خطا",
                    weaknesses = listOf(
                        SubjectPerformance(name = "ادبیات", percentage = 46),
                        SubjectPerformance(name = "عربی", percentage = 54),
                        SubjectPerformance(name = "زبان انگلیسی", percentage = 58),
                    ),
                    strengths = listOf(
                        SubjectPerformance(name = "زیست شناسی", percentage = 92),
                        SubjectPerformance(name = "شیمی", percentage = 86),
                        SubjectPerformance(name = "ریاضی", percentage = 80),
                    ),
                    studyDistribution = listOf(
                        StudyDistributionPoint(timeSlot = "۰-۴", hours = 0.4f),
                        StudyDistributionPoint(timeSlot = "۴-۸", hours = 2.0f),
                        StudyDistributionPoint(timeSlot = "۸-۱۲", hours = 3.5f),
                        StudyDistributionPoint(timeSlot = "۱۲-۱۶", hours = 4.0f, isPeak = true),
                        StudyDistributionPoint(timeSlot = "۱۶-۲۰", hours = 2.8f),
                        StudyDistributionPoint(timeSlot = "۲۰-۲۴", hours = 1.4f),
                    ),
                    peakStudyHoursBadge = "۴ ساعت"
                )
            }
        }
    }

    fun selectStrengthsTab(tab: AnalysisTabType) {
        _uiState.update { it.copy(activeStrengthsTab = tab) }
    }
}
