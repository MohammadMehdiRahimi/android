# Tasks: Create Study Plan Pixel-Perfect Redesign

## Phase 1: Models & Domain
- [x] Define `StudySessionUiModel`, `SubjectCategory`, and `WeekDayItem` in `CreateStudyPlanViewModel.kt`.
- [x] Add Persian vector subject icons and helpers (biology leaf, math radical, chemistry flask, physics atom, literature book, review checklist).
- [x] Connect Jalali date generation to provide full Persian calendar week days around `selectedDate`.

## Phase 2: State Management & ViewModel Logic
- [x] Add default daily sessions to state matching the reference design (زیست شناسی, ریاضی, شیمی, فیزیک, ادبیات فارسی, مرور و تست) with realistic timestamps and durations.
- [x] Implement session toggle completion (`toggleSessionCompletion(sessionId)`) and dynamically recalculate progress percentage, completed session counts, and total study minutes.
- [x] Implement week navigation (`selectDate(jalaliDate)`, `goToPreviousWeek()`, `goToNextWeek()`).
- [x] Implement add/edit/delete session actions (`addSession(...)`, `deleteSession(sessionId)`).
- [x] Implement "کپی از روز قبل" (copy yesterday's sessions to today) and "ذخیره برنامه روز" (persist sessions locally / API).

## Phase 3: Pixel-Perfect Jetpack Compose Components
- [x] **`CreatePlanTopBar`**:
  - Implement top bar with back navigation button, Persian title «برنامه‌ریزی برای علی محمدی», and subtitle «برنامه درسی و مطالعه».
  - Strictly omit user profile avatar as requested.
- [x] **`WeekDaysSelectorRow`**:
  - Implement the horizontal calendar row with calendar icon, previous/next arrows, and day items.
  - Implement purple gradient card for selected day with indicator dot.
- [x] **`DaySummaryMetricsCard`**:
  - Implement 3-column metrics card: Total study time, session count, and circular progress indicator with percentage.
- [x] **`StudySessionItemCard`**:
  - Implement session card with rounded checkbox, subject-colored icon, subject and chapter titles, status badges («انجام شده» green badge, «بعدی» purple badge), start time, duration, and overflow menu.
  - Apply subtle purple border when session is marked as `isNext`.
- [x] **`AddSessionOutlineButton`**:
  - Implement full-width outlined button «+ افزودن جلسه» with purple border.
- [x] **`AddSessionBottomSheet`**:
  - Implement bottom sheet dialog for adding new study sessions with subject, chapter, time, and duration inputs.
- [x] **`StickyBottomActionsBar`**:
  - Implement bottom actions container with primary purple «ذخیره برنامه روز ✓» button and secondary outlined «کپی از روز قبل» button.

## Phase 4: Screen Integration & Polish
- [x] Update `CreateStudyPlanScreen.kt` to assemble all components in a scrollable `LazyColumn` with sticky bottom bar.
- [x] Ensure strict RTL (`LocalLayoutDirection provides LayoutDirection.Rtl`), Persian typography, and Persian numbers across all elements.
- [x] Apply semantic `Modifier.testTag` identifiers across all interactive elements.

## Phase 5: Verification & Testing
- [x] Write unit tests in `PixelPerfectStudyPlanTest.kt` verifying state updates, session completion toggling, progress calculation, and copy day actions.
- [x] Verify state transitions, week switching, and dynamic progress bar calculations.
- [x] Run `compile_applet` to verify compilation and build integrity.

