package com.example.ui.features.academicreport

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AnalyzerViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AnalyzerUiState())
    val uiState: StateFlow<AnalyzerUiState> = _uiState.asStateFlow()

    fun selectTimeframe(timeframe: AnalyzerTimeframe) {
        if (_uiState.value.selectedTimeframe == timeframe) return

        _uiState.update { currentState ->
            when (timeframe) {
                AnalyzerTimeframe.LAST_WEEK -> currentState.copy(
                    selectedTimeframe = timeframe,
                    aiScoreImprovement = "۲.۲٪",
                    aiInsightParagraphs = listOf(
                        "عملکرد شما نسبت به هفته قبل ۲.۲٪ بهتر شده است.",
                        "در درس ریاضی و فیزیک پیشرفت خوبی داشته‌اید.",
                        "برای افزایش درصد توصیه‌می‌شود روی مباحث ادبیات تمرکز بیشتری داشته باشید."
                    ),
                    aiInsights = listOf(
                        AiInsightItem(title = "پیشنهاد ما", value = "مرور مباحث ادبیات", type = InsightType.RECOMMENDATION),
                        AiInsightItem(title = "بیشترین تمرکز", value = "شب ها (۲۰-۲۲)", type = InsightType.PEAK_FOCUS),
                        AiInsightItem(title = "سبک یادگیری", value = "دیداری", type = InsightType.LEARNING_STYLE),
                    ),
                    metrics = listOf(
                        MetricCardData(
                            title = "تعداد آزمون",
                            value = 6,
                            subtitle = "۴ آزمون",
                            trend = MetricTrend.NEUTRAL,
                            iconType = MetricIconType.EXAM_COUNT,
                        ),
                        MetricCardData(
                            title = "تست غلط",
                            value = 158,
                            subtitle = "▼ ۸٪",
                            trend = MetricTrend.NEGATIVE,
                            iconType = MetricIconType.WRONG_TESTS,
                        ),
                        MetricCardData(
                            title = "تست صحیح",
                            value = 328,
                            subtitle = "۶۷٪ موفقیت",
                            trend = MetricTrend.NEUTRAL,
                            iconType = MetricIconType.CORRECT_TESTS,
                        ),
                        MetricCardData(
                            title = "تعداد تست",
                            value = 486,
                            subtitle = "▲ ۱۵٪",
                            trend = MetricTrend.POSITIVE,
                            iconType = MetricIconType.TOTAL_TESTS,
                        ),
                    ),
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
                        StudyDistributionPoint(timeSlot = "۱۲-۱۶", hours = 3.0f, isPeak = true),
                        StudyDistributionPoint(timeSlot = "۱۶-۲۰", hours = 1.6f),
                        StudyDistributionPoint(timeSlot = "۲۰-۲۴", hours = 0.6f),
                    )
                )
                AnalyzerTimeframe.LAST_MONTH -> currentState.copy(
                    selectedTimeframe = timeframe,
                    aiScoreImprovement = "۵.۸٪",
                    aiInsightParagraphs = listOf(
                        "در ماه گذشته پایداری مطالعه شما به طور چشمگیری افزایش یافته است.",
                        "تعداد تست‌های درس زیست‌شناسی و شیمی به بیشترین حد در این دوره رسیده است.",
                        "پیشنهاد می‌شود زمان حل تست درس فیزیک را در نوبت صبحگاهی افزایش دهید."
                    ),
                    aiInsights = listOf(
                        AiInsightItem(title = "پیشنهاد ما", value = "تست ترکیبی شیمی و زیست", type = InsightType.RECOMMENDATION),
                        AiInsightItem(title = "بیشترین تمرکز", value = "عصرها (۱۶-۱۹)", type = InsightType.PEAK_FOCUS),
                        AiInsightItem(title = "سبک یادگیری", value = "تحلیلی - دیداری", type = InsightType.LEARNING_STYLE),
                    ),
                    metrics = listOf(
                        MetricCardData(
                            title = "تعداد آزمون",
                            value = 24,
                            subtitle = "۱۸ آزمون جامع",
                            trend = MetricTrend.POSITIVE,
                            iconType = MetricIconType.EXAM_COUNT,
                        ),
                        MetricCardData(
                            title = "تست غلط",
                            value = 540,
                            subtitle = "۱۲٪ کاهش غلط‌ها",
                            trend = MetricTrend.POSITIVE,
                            iconType = MetricIconType.WRONG_TESTS,
                        ),
                        MetricCardData(
                            title = "تست صحیح",
                            value = 1420,
                            subtitle = "۷۲٪ درصد موفقیت",
                            trend = MetricTrend.POSITIVE,
                            iconType = MetricIconType.CORRECT_TESTS,
                        ),
                        MetricCardData(
                            title = "تعداد تست",
                            value = 1960,
                            subtitle = "۲۲٪ رشد ماهانه",
                            trend = MetricTrend.POSITIVE,
                            iconType = MetricIconType.TOTAL_TESTS,
                        ),
                    ),
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
                        StudyDistributionPoint(timeSlot = "۰-۴", hours = 0.3f),
                        StudyDistributionPoint(timeSlot = "۴-۸", hours = 1.8f),
                        StudyDistributionPoint(timeSlot = "۸-۱۲", hours = 3.2f),
                        StudyDistributionPoint(timeSlot = "۱۲-۱۶", hours = 3.6f, isPeak = true),
                        StudyDistributionPoint(timeSlot = "۱۶-۲۰", hours = 2.4f),
                        StudyDistributionPoint(timeSlot = "۲۰-۲۴", hours = 1.1f),
                    )
                )
                AnalyzerTimeframe.LAST_3_MONTHS -> currentState.copy(
                    selectedTimeframe = timeframe,
                    aiScoreImprovement = "۱۲.۴٪",
                    aiInsightParagraphs = listOf(
                        "روند کلی ۳ ماهه شما نشان‌دهنده جهش تراز در آزمون‌های آزمایشی است.",
                        "تداوم و تسلط بالا در دروس تخصصی تجربی / ریاضی تثبیت شده است.",
                        "توصیه اکید: مرور روتین‌های روزانه واژگان و درک مطلب عربی."
                    ),
                    aiInsights = listOf(
                        AiInsightItem(title = "پیشنهاد ما", value = "مرور خلاصه نویسی‌ها", type = InsightType.RECOMMENDATION),
                        AiInsightItem(title = "بیشترین تمرکز", value = "ظهرها (۱۲-۱۶)", type = InsightType.PEAK_FOCUS),
                        AiInsightItem(title = "سبک یادگیری", value = "آزمون‌محور", type = InsightType.LEARNING_STYLE),
                    ),
                    metrics = listOf(
                        MetricCardData(
                            title = "تعداد آزمون",
                            value = 68,
                            subtitle = "۵۴ آزمون هدفمند",
                            trend = MetricTrend.POSITIVE,
                            iconType = MetricIconType.EXAM_COUNT,
                        ),
                        MetricCardData(
                            title = "تست غلط",
                            value = 1480,
                            subtitle = "۱۹٪ کاهش نرخ خطا",
                            trend = MetricTrend.POSITIVE,
                            iconType = MetricIconType.WRONG_TESTS,
                        ),
                        MetricCardData(
                            title = "تست صحیح",
                            value = 4620,
                            subtitle = "۷۵٪ درصد موفقیت",
                            trend = MetricTrend.POSITIVE,
                            iconType = MetricIconType.CORRECT_TESTS,
                        ),
                        MetricCardData(
                            title = "تعداد تست",
                            value = 6100,
                            subtitle = "۳۵٪ رشد تجمعی",
                            trend = MetricTrend.POSITIVE,
                            iconType = MetricIconType.TOTAL_TESTS,
                        ),
                    ),
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
                        StudyDistributionPoint(timeSlot = "۱۲-۱۶", hours = 3.8f, isPeak = true),
                        StudyDistributionPoint(timeSlot = "۱۶-۲۰", hours = 2.8f),
                        StudyDistributionPoint(timeSlot = "۲۰-۲۴", hours = 1.4f),
                    )
                )
            }
        }
    }
}
