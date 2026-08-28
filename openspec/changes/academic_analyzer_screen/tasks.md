# Actionable Tasks: Academic Analyzer Screen Implementation

- [x] **1. Architecture & State Management (`AcademicReportViewModel.kt`)**
  - [x] Define `AnalyzerUiState` with timeframe selection, AI analysis text, key performance metrics, subject strengths/weaknesses list, daily study distribution chart data points, and user profile info.
  - [x] Implement timeframe switcher (هفته گذشته, ماه گذشته, ۳ ماه گذشته) with reactive state updates.

- [x] **2. Reusable Visual Components (`AnalyzerComponents.kt`)**
  - [x] Build `AnalyzerTopHeader` with profile avatar, active status indicator, title/subtitle, and notification button.
  - [x] Build `TimeframeFilterBar` with interactive pills and calendar icon dropdown.
  - [x] Build `AiSmartAnalysisCard` with AI robot graphic / illustration, speech bubble icon, analysis narrative, and the 3 bottom chips (پیشنهاد ما, بیشترین تمرکز, سبک یادگیری).
  - [x] Build `PerformanceMetricsGrid` for the 4 stat cards (تعداد آزمون, تست غلط, تست صحیح, تعداد تست) with percentage changes and icons.
  - [x] Build `StrengthsAndWeaknessesSection` displaying side-by-side cards with customized progress bars for subjects (زیست‌شناسی, شیمی, ریاضی vs فیزیک, ادبیات, عربی) and "مشاهده جزئیات" links.
  - [x] Build `DailyStudyDistributionChart` using Jetpack Compose `Canvas` with smooth bezier curve, hourly Y-axis lines, X-axis time blocks (۰-۴ تا ۲۰-۲۴), points, and the "۳ ساعت" peak tooltip badge.
  - [x] Build `PeriodicReportsBanner` featuring clipboard graphic, title/subtitle, "دریافت گزارش" download button, and "مقایسه با دوستان" compare button.

- [x] **3. Screen Integration & Navigation (`AcademicReportScreen.kt`)**
  - [x] Assemble all sections into a smooth, lazy-loaded `LazyColumn` with responsive RTL layout and edge-to-edge support.
  - [x] Ensure navigation integration with the bottom navigation bar and notification/league routes.

- [x] **4. Verification & Testing**
  - [x] Build and verify with `compile_applet`.
  - [x] Add unit tests for `AnalyzerViewModel` (`AnalyzerViewModelTest.kt`).
