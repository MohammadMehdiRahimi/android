# Tasks: Exam Builder 3-Step Wizard with Redesigned Step 1 (مراحل پیاده‌سازی)

- [x] 1. **Ensure FAB & Card Arrow Adjustments in `ExamsScreen`**
  - [x] 1.1 Verify FAB is floating at bottom-start (left in RTL) and clicking it navigates directly to `build_exam`.
  - [x] 1.2 Verify arrow icons in exam cards are left-pointing (`Icons.AutoMirrored.Filled.KeyboardArrowLeft`).

- [x] 2. **Implement 3-Step Wizard Structure in `BuildExamScreen.kt`**
  - [x] 2.1 Refactor wizard navigation to support 3 distinct stages:
    - Step 1: `۱. ساختار آزمون` (Exam Structure)
    - Step 2: `۲. تنظیم سوالات` (Question Settings)
    - Step 3: `۳. ساخت آزمون` (Finalize & Launch)
  - [x] 2.2 Implement top stepper component with progress circles and Persian labels.

- [x] 3. **Implement Step 1 UI Components (`exam-create-1.png`)**
  - [x] 3.1 Build top bar with back navigation, Persian titles, and help `?` button.
  - [x] 3.2 Implement "نوع آزمون" toggle section ("آزمون تستی" / "آزمون تشریحی").
  - [x] 3.3 Implement "پایه و رشته" dual dropdown selector.
  - [x] 3.4 Implement "کتاب‌ها و محدوده آزمون" list with selected book cards, cover thumbnails, chapter subtitles, topic tags, delete button, and "+ افزودن کتاب دیگر" dialog/sheet.
  - [x] 3.5 Implement "منبع سوالات" selector ("تألیفی", "سوالات کنکور", "سوالات نهایی").
  - [x] 3.6 Implement "خلاصه انتخاب‌های شما" 4-column summary card with reactive counts.
  - [x] 3.7 Implement bottom actions ("ادامه به مرحله بعد" and "انصراف").

- [x] 4. **Connect Step 2 and Step 3 Flows**
  - [x] 4.1 Transition seamlessly from Step 1 to Step 2 (Question counts & difficulty levels).
  - [x] 4.2 Transition from Step 2 to Step 3 (Exam name, duration, negative scoring, start exam).

- [x] 5. **Testing & Verification**
  - [x] 5.1 Write/update unit and Robolectric UI tests for the 3-step wizard and Step 1 components.
  - [x] 5.2 Compile and verify applet build.
