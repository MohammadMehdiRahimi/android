# Tasks: Exams List Skeleton Loading, RTL Arrow Fix & Step 3 Summary Redesign

- [ ] **1. Exams List Screen (`ExamsScreen.kt` & `ExamItemCard.kt`)**
  - [ ] **1.1. Granular Skeleton Loading**:
    - Update `ExamsScreen.kt` so static content (Header, Search, Filter chips, "تعداد کل" label) is rendered immediately.
    - Show shimmer effect only for the count value and display `ExamItemCardSkeleton` placeholders for list items during loading.
  - [ ] **1.2. Card Navigation Arrow**:
    - In `ExamItemCard.kt`, change the forward navigation arrow to a left arrow (`Icons.AutoMirrored.Filled.KeyboardArrowLeft` or `Icons.Filled.KeyboardArrowLeft`) for Persian RTL compliance.

- [ ] **2. Step 3 Summary Screen Redesign (`exam-create-3.png`)**
  - [ ] **2.1. Stepper & Header**:
    - Render 3-step timeline with steps 1 & 2 marked complete (checkmark) and step 3 active with Persian numbering.
  - [ ] **2.2. General Exam Summary (خلاصه کلی آزمون)**:
    - Implement 2x4 card grid showing (نوع آزمون, پایه, رشته, تعداد کتاب‌ها, منبع سوال, نمره منفی, چینش سوالات, مدت زمان تقریبی) with distinct icons and Persian typography.
  - [ ] **2.3. Question Statistics (آمار سوالات آزمون)**:
    - Implement question count summary, color-coded difficulty indicators (آسان, متوسط, دشوار, خیلی دشوار), and multi-colored segmented horizontal bar.
  - [ ] **2.4. Exam Sections Breakdown (بخش‌های آزمون)**:
    - Build book cards featuring book cover art, chapter title, topic pill tags, total questions badge, and per-book difficulty breakdown with colored indicators.
  - [ ] **2.5. Exam Tips Section (نکات آزمون)**:
    - Implement guidelines card with circular icons, bullet items, and dashed connector lines.
  - [ ] **2.6. Bottom Action & Navigation Insets**:
    - Add "ساخت آزمون" button with sparkle icon, "مرحله قبل" link, and `navigationBarsPadding` + spacing.

- [ ] **3. Verification & Testing**
  - [ ] Update / run Robolectric unit tests in `BuildExamWizardTest.kt` and `ExamsScreenTest.kt`.
  - [ ] Verify build with `compile_applet`.
