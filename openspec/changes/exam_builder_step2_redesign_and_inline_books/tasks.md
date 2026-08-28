# Tasks: Exam Builder Step 2 Redesign & Step 1 Inline Book Selection

## Phase 2 Implementation Tasks (مراحل پیاده‌سازی)

### Task 1: Step 1 Inline Book Picker (حذف مودال و افزودن اینلاین کتاب)
- [ ] Refactor `Step1ExamStructureScreen.kt` to remove the `Dialog` / `AddBookModalBox`.
- [ ] Implement `InlineAddBookBox` directly inside the scrollable column below the books list.
- [ ] Provide book selection, chapter dropdown, interactive topic chips, and confirm button inside the page.

### Task 2: Step 2 Pixel-Perfect Implementation (`Step2QuestionSettingsScreen.kt`)
- [ ] Design the Top Bar with title `طراحی آزمون جدید`, subtitle `مرحله ۲: تنظیم سوالات`, and help/back icons.
- [ ] Implement Stepper component showing Step 1 checked, Step 2 active, Step 3 inactive.
- [ ] Implement 5-column `ExamSummaryHeaderCard` matching `exam-create-2.png`.
- [ ] Implement `BookQuestionSettingsCard` with:
  - Book thumbnail cover and metadata (Title, Chapter).
  - Red `حذف` delete button.
  - Topic chips list under `موضوعات انتخابی:`.
  - 4 difficulty stepper counters (`آسان`, `متوسط`, `دشوار`, `خیلی دشوار`) with interactive `+` and `-` controls.
  - Reactive bottom text `جمع سوالات این بخش: X سوال`.
- [ ] Implement `ExamSettingsOptionsCard` with switches for `نمره منفی` and `چینش تصادفی سوالات`.
- [ ] Implement bottom action buttons (`ادامه به مرحله بعد` & `بازگشت`).

### Task 3: Integration & Testing
- [ ] Update `BuildExamScreen.kt` to bind Step 1 and Step 2 states seamlessly.
- [ ] Update and execute Unit/Compose tests in `BuildExamWizardTest.kt`.
- [ ] Verify compilation with `compile_applet`.
