package com.example.ui.features.academicreport

enum class AnalyzerTimeframe(val title: String) {
    LAST_WEEK("هفته گذشته"),
    LAST_MONTH("ماه گذشته"),
    LAST_3_MONTHS("۳ ماه گذشته")
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
    PEAK_FOCUS,
    LEARNING_STYLE
}

data class AnalyzerUiState(
    val selectedTimeframe: AnalyzerTimeframe = AnalyzerTimeframe.LAST_WEEK,
    val userName: String = "سینا رحیمی",
    val isUserActive: Boolean = true,
    val unreadNotificationsCount: Int = 2,
    val aiScoreImprovement: String = "۲.۲٪",
    val aiInsightParagraphs: List<String> = listOf(
        "عملکرد شما نسبت به هفته قبل ۲.۲٪ بهتر شده است.",
        "در درس ریاضی و فیزیک پیشرفت خوبی داشته‌اید.",
        "برای افزایش درصد توصیه‌می‌شود روی مباحث ادبیات تمرکز بیشتری داشته باشید."
    ),
    val aiInsights: List<AiInsightItem> = listOf(
        AiInsightItem(title = "پیشنهاد ما", value = "مرور مباحث ادبیات", type = InsightType.RECOMMENDATION),
        AiInsightItem(title = "بیشترین تمرکز", value = "شب ها (۲۰-۲۲)", type = InsightType.PEAK_FOCUS),
        AiInsightItem(title = "سبک یادگیری", value = "دیداری", type = InsightType.LEARNING_STYLE),
    ),
    val metrics: List<MetricCardData> = listOf(
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
        StudyDistributionPoint(timeSlot = "۱۲-۱۶", hours = 3.0f, isPeak = true),
        StudyDistributionPoint(timeSlot = "۱۶-۲۰", hours = 1.6f),
        StudyDistributionPoint(timeSlot = "۲۰-۲۴", hours = 0.6f),
    ),
    val isLoading: Boolean = false,
)
