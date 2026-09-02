# Tasks: Academic Report Screen Redesign (صفحه تحلیل)

## Phase 2: Implementation Checklist

- [x] **1. Asset Relocation & Integration:**
    - [x] Move `/ai-vector.png` to `app/src/main/res/drawable/ai_vector.png`.
    - [x] Verify drawable is accessible in Compose via `R.drawable.ai_vector`.

- [x] **2. Presentation & State Layer:**
    - [x] Update `AnalyzerModels.kt`, `AnalyzerViewModel.kt`, and `AcademicReportUiState` to support the new metrics, period filters, and strengths/weaknesses data.
    - [x] Rewrite `AcademicReportScreen.kt` and `AnalyzerComponents.kt` from scratch to strictly follow the UI screenshot:
        - [x] Header with Avatar + Online indicator, Title/Subtitle, and Notification Bell button.
        - [x] 3-Item Time Range Filter row (هفته گذشته، ۳ ماه گذشته، ماه گذشته).
        - [x] "تحلیل هوشمند" (AI Smart Analysis) card with `ai_vector` image, speech sparkle, natural language summary, and 3 suggestion mini-cards (پیشنهاد ما، بهترین تمرکز، سبک یادگیری).
        - [x] 2x2 Performance Stat Cards grid (تست صحیح، تعداد تست، تعداد آزمون، تست غلط).
        - [x] Strengths & Weaknesses toggle tabs with competency progress bars and "مشاهده جزئیات".
        - [x] "توزیع زمان مطالعه در طول روز" line chart with curve, area gradient, and Persian axes.
        - [x] "گزارش‌های دوره‌ای" download and compare action card.

- [x] **3. Navigation & Screen Integration:**
    - [x] Ensure `AcademicReportScreen` is properly wired to bottom navigation ("تحلیل" tab) and NavHost in `MainActivity.kt` and `MainScreen.kt`.

- [x] **4. Testing & Verification:**
    - [x] Update ViewModel and UI tests in `AnalyzerViewModelTest.kt`.
    - [x] Compile and verify applet build.
