# Tasks: Weekly Study Plan UI Refinements & Session Type Selector

- [ ] **Phase 1: Entities & State Definition**
  - [ ] Add `StudySessionType` enum (`EXAM`, `LEARNING`, `REVIEW`, `OTHER`) with Persian display titles ("آزمون", "آموزش", "مرور", "سایر").
  - [ ] Update `StudySessionUiModel` to include `sessionType: StudySessionType = StudySessionType.LEARNING`.

- [ ] **Phase 2: ViewModel Updates (`CreateStudyPlanViewModel.kt`)**
  - [ ] Update `generateWeekDaysForDate` to center around `selected` date using `(-3..3).map { ... }` so today is at the center (index 3).
  - [ ] Update `addStudySession` and `updateStudySession` to receive and store `sessionType`.

- [ ] **Phase 3: UI Updates in `PixelPerfectPlanComponents.kt`**
  - [ ] **Centered Week Selector:** Ensure `PixelPerfectWeekSelector` centers today/selected day in the view.
  - [ ] **Metric Card Swap:** Swap the columns in `PixelPerfectDailySummaryCard` so "تعداد جلسات" is on the right and "کل زمان مطالعه" is on the left.
  - [ ] **Session Card Badge:** Remove the start time badge (`session.startTime`) from `PixelPerfectSessionCard` and keep only the duration badge.
  - [ ] **Session Type Selector:** Add the "نوع جلسه" selectable chip group above the scheduling section in `AddStudySessionModal`.

- [ ] **Phase 4: Screen & Callback Integration**
  - [ ] Update `CreateStudyPlanScreen.kt` callbacks where `AddStudySessionModal` is invoked to pass and handle `sessionType`.

- [ ] **Phase 5: Tests & Verification**
  - [ ] Update/Add unit tests in `PixelPerfectStudyPlanTest.kt` verifying:
    - Today is centered in `weekDays` (index 3 out of 7 items).
    - Summary card metrics order and content.
    - Session card displays duration without start time text.
    - Session type enum and selection flow.
  - [ ] Run `gradle :app:testDebugUnitTest --tests "com.example.ui.features.studyplan.PixelPerfectStudyPlanTest"` to verify all tests pass.
  - [ ] Run `compile_applet` to ensure clean build.
