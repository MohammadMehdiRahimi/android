package com.example.ui.features.academicreport

enum class AnalyzerTimeframe(val title: String) {
    LAST_WEEK("هفته گذشته"),
    LAST_3_MONTHS("۳ ماه گذشته"),
    LAST_MONTH("ماه گذشته")
}

enum class AnalysisTabType {
    STRENGTHS,
    WEAKNESSES
}

enum class MetricTrend {
    POSITIVE,
    NEGATIVE,
    NEUTRAL
}

enum class MetricIconType {
    EXAM_COUNT,
    WRONG_TESTS,
    CORRECT_TESTS,
    TOTAL_TESTS
}

data class MetricCardData(
    val title: String,
    val value: Int,
    val subtitle: String,
    val trend: MetricTrend = MetricTrend.NEUTRAL,
    val iconType: MetricIconType,
)

data class SubjectPerformance(
    val name: String,
    val percentage: Int,
)

data class StudyDistributionPoint(
    val timeSlot: String,
    val hours: Float,
    val isPeak: Boolean = false,
)

data class AiInsightItem(
    val title: String,
    val value: String,
    val type: InsightType,
)

enum class InsightType {
    RECOMMENDATION,
    PEAK_FOCUS
}

data class AnalyzerUiState(
    val selectedTimeframe: AnalyzerTimeframe = AnalyzerTimeframe.LAST_WEEK,
    val userName: String = "پوریا",
    val isUserActive: Boolean = true,
    val aiInsightParagraphs: List<String> = listOf(
        "عملکرد شما نسبت به هفته قبل ۷.۲٪ بهتر شده است.",
        "در درس ریاضی و فیزیک پیشرفت خوبی داشته‌اید.",
        "برای افزایش درصد توصیه‌می‌شود روی مباحث ادبیات تمرکز بیشتری داشته باشید."
    ),
    val aiInsights: List<AiInsightItem> = listOf(
        AiInsightItem(title = "پیشنهاد ما", value = "مرور مباحث ادبیات", type = InsightType.RECOMMENDATION),
        AiInsightItem(title = "بهترین تمرکز", value = "شب ها (۲۰-۲۴)", type = InsightType.PEAK_FOCUS),
    ),
    val correctTestsCount: Int = 328,
    val correctTestsSubtitle: String = "۶۷.۳ درصد موفقیت",
    val totalTestsCount: Int = 486,
    val totalTestsSubtitle: String = "▲ ۱۵٪ نسبت به هفته قبل",
    val totalExamsCount: Int = 6,
    val totalExamsSubtitle: String = "۴ آزمون",
    val wrongTestsCount: Int = 158,
    val wrongTestsSubtitle: String = "▼ ۸٪ نسبت به هفته قبل",
    val activeStrengthsTab: AnalysisTabType = AnalysisTabType.STRENGTHS,
    val weaknesses: List<SubjectPerformance> = listOf(
        SubjectPerformance(name = "فیزیک", percentage = 31),
        SubjectPerformance(name = "ادبیات", percentage = 42),
        SubjectPerformance(name = "عربی", percentage = 45),
    ),
    val strengths: List<SubjectPerformance> = listOf(
        SubjectPerformance(name = "زیست شناسی", percentage = 84),
        SubjectPerformance(name = "شیمی", percentage = 78),
        SubjectPerformance(name = "ریاضی", percentage = 72),
    ),
    val studyDistribution: List<StudyDistributionPoint> = listOf(
        StudyDistributionPoint(timeSlot = "۰-۴", hours = 0.8f),
        StudyDistributionPoint(timeSlot = "۴-۸", hours = 1.4f),
        StudyDistributionPoint(timeSlot = "۸-۱۲", hours = 2.3f),
        StudyDistributionPoint(timeSlot = "۱۲-۱۶", hours = 3.6f, isPeak = true),
        StudyDistributionPoint(timeSlot = "۱۶-۲۰", hours = 2.0f),
        StudyDistributionPoint(timeSlot = "۲۰-۲۴", hours = 0.6f),
    ),
    val peakStudyHoursBadge: String = "۳ ساعت",
    val isLoading: Boolean = false,
)
