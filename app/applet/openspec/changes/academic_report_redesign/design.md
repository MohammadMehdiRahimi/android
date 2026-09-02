# Design: Academic Report Screen Redesign (صفحه تحلیل)

## 1. Architecture & Layers
*   **Presentation Layer (`ui/features/academicreport/`):**
    *   `AcademicReportScreen.kt`: Complete Compose UI rendering the pixel-perfect layout matching the provided screenshot.
    *   `AcademicReportViewModel.kt` (or `AnalyzerViewModel.kt`): Holds state for selected period filter (weekly, 3 months, monthly), selected strength/weakness tab, metrics, chart points, and AI summary.
    *   `AcademicReportUiState.kt`: Immutable UI state with all metric values, AI recommendations, study time distribution data points, and strength/weakness progress lists.
*   **Asset Management:**
    *   Move `/ai-vector.png` to `app/src/main/res/drawable/ai_vector.png`.
    *   Integrate `painterResource(id = R.drawable.ai_vector)` inside the AI analysis hero card.

## 2. Component Design & Layout Hierarchy
1.  **Header (`AnalysisTopBar`):**
    *   User avatar on start with a 10.dp green circle badge at the bottom-right.
    *   Title Column with "تحلیل" (Bold 20.sp) and "گزارش کامل پیشرفت شما" (12.sp, grey).
    *   Rounded square notification button with badge dot on end.
2.  **Filter Tabs (`TimePeriodFilterBar`):**
    *   3 tabs: "هفته گذشته" (selected: `#5B42F3`, dropdown + calendar icon), "۳ ماه گذشته" (unselected), "ماه گذشته" (unselected).
3.  **Smart AI Card (`SmartAiInsightCard`):**
    *   Card with soft border and subtle gradient background.
    *   Top right: "Ai" purple badge + "تحلیل هوشمند" bold text.
    *   Row containing the `ai_vector` robot image on the right/left and AI natural language advice on the other side.
    *   Speech bubble accent with sparkles.
    *   3 rounded mini-cards below:
        *   "پیشنهاد ما" / "مرور مباحث ادبیات" (Red target icon)
        *   "بهترین تمرکز" / "شب‌ها (۲۰-۲۵)" (Purple moon icon)
        *   "سبک یادگیری" / "دیداری" (Purple eye icon)
4.  **2x2 Performance Grid (`StatsGrid`):**
    *   `Row` of 2 cards and another `Row` of 2 cards with identical height and soft elevation.
    *   Each card features an icon in a light pastel circle, main Persian counter, title, and delta/progress footnote with up/down arrows.
5.  **Strengths & Weaknesses Card (`StrengthsWeaknessesCard`):**
    *   Segmented header tab: "نقاط قوت" (active green underline + thumbs up) vs "نقاط ضعف" (thumbs down).
    *   Vertical list of subjects with Persian names, progress indicators, and percentage labels.
    *   Footer with "مشاهده جزئیات" and arrow icon.
6.  **Daily Study Time Distribution (`StudyTimeDistributionCard`):**
    *   Title with clock icon and peak duration pill badge ("۳ ساعت").
    *   Custom Compose Canvas line chart with cubic bezier smooth curve, gradient fill under the line, dot points at each timestamp, and Persian labels on X/Y axes.
7.  **Periodic Reports Card (`PeriodicReportsCard`):**
    *   Card with report illustration, header, and dual action buttons ("دریافت گزارش" & "مقایسه با دوستان").

## 3. Localization & RTL
*   Full RTL layout support via `LocalLayoutDirection provides LayoutDirection.Rtl`.
*   All numbers formatted with Persian digits (`toPersianNumber()`).
*   `IranSansFontFamily` applied across all text components.
