# Tasks: Academic Report Screen Redesign (صفحه تحلیل)

## Phase 2: Implementation Checklist

- [ ] **1. Asset Relocation & Integration:**
    - [ ] Move `/ai-vector.png` to `app/src/main/res/drawable/ai_vector.png`.
    - [ ] Verify drawable is accessible in Compose via `R.drawable.ai_vector`.

- [ ] **2. Presentation & State Layer:**
    - [ ] Update `AcademicReportUiState.kt` and `AnalyzerViewModel.kt` (or `AcademicReportViewModel.kt`) to support the new metrics, period filters, and strengths/weaknesses data.
    - [ ] Rewrite `AcademicReportScreen.kt` from scratch to strictly follow the new UI screenshot:
        - [ ] Header with Avatar + Online indicator, Title/Subtitle, and Notification Bell button.
        - [ ] 3-Item Time Range Filter row.
        - [ ] "تحلیل هوشمند" (AI Smart Analysis) card with `ai_vector` image, natural language summary, and 3 suggestion mini-cards (پیشنهاد ما، بهترین تمرکز، سبک یادگیری).
        - [ ] 2x2 Performance Stat Cards grid (تست صحیح، تعداد تست، تعداد آزمون، تست غلط).
        - [ ] Strengths & Weaknesses toggle tabs with competency progress bars and "مشاهده جزئیات".
        - [ ] "توزیع زمان مطالعه در طول روز" line chart with curve, area gradient, and Persian axes.
        - [ ] "گزارش‌های دوره‌ای" download and compare action card.

- [ ] **3. Navigation & Screen Integration:**
    - [ ] Ensure `AcademicReportScreen` is properly wired to bottom navigation ("تحلیل" tab) and NavHost in `MainActivity.kt` and `MainScreen.kt`.

- [ ] **4. Testing & Verification:**
    - [ ] Update ViewModel and UI tests in `AnalyzerViewModelTest.kt` or `AcademicReportScreenTest.kt`.
    - [ ] Compile and verify applet build.
