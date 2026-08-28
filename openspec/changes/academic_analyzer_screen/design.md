# Feature Design: Academic Analyzer Screen (صفحه تحلیلگر)

## 1. Architectural Alignment
Following the Clean Architecture & Feature-First principles in `openspec/project.md`:
- **UI & Presentation Layer (`com.example.ui.features.academicreport` or `analyzer`):**
  - `AcademicReportScreen.kt`: The main screen orchestrating the state and modular components.
  - `AnalyzerComponents.kt`: Reusable composables including:
    - `AnalyzerTopHeader`: Avatar + User Status + Title/Subtitle + Notification Bell.
    - `TimeframeFilterBar`: Horizontal filter pills (Week, Month, 3 Months).
    - `AiSmartAnalysisCard`: 3D Robot illustration, speech bubble, AI badge, summary text, and 3 quick insight chips.
    - `PerformanceMetricsGrid`: 4 statistic cards with icons, values, trends (positive green / negative red).
    - `StrengthsAndWeaknessesSection`: Side-by-side strengths (green) and weaknesses (red) progress indicators.
    - `DailyStudyDistributionChart`: Custom `Canvas` smooth bezier line chart with points, Y-axis hours, X-axis time slots, and peak pill tooltip.
    - `PeriodicReportsBanner`: Promotional card with action buttons for report download and peer comparison.
  - `AcademicReportViewModel.kt` / `AnalyzerViewModel.kt`: State management handling selected timeframe, metrics, subject percentages, and chart data.

## 2. Visual Design & Theme Palette
- **Primary Purple**: `#7C3AED` / `#6D28D9`
- **Soft Lavender Background**: `#F8F6FE` / `#EDE9FE`
- **Success Green**: `#10B981` / `#D1FAE5`
- **Warning / Error Red**: `#EF4444` / `#FEE2E2`
- **Orange / Wrong Accent**: `#F59E0B` / `#FEF3C7`
- **Card Borders & Shadows**: Subtle 1.dp `#F1F5F9` borders with light elevation.

## 3. RTL, Localization & Typography
- Persian fonts with `IranSansFontFamily` or system Persian typography.
- All numbers formatted via `.toPersianNumber()`.
- Layout direction strictly `LayoutDirection.Rtl`.
