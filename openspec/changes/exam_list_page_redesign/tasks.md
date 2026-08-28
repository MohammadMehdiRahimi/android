# Tasks: Exam Maker List Screen Redesign (مراحل پیاده‌سازی صفحه لیست آزمون‌های من)

- [ ] 1. **Data Models & State Setup**
  - [ ] 1.1 Create/update `ExamListItem` and `ExamType` domain models in `com.example.ui.features.exams`.
  - [ ] 1.2 Define `ExamsUiState` and `FilterType` (Date, Subject, Topic).
  - [ ] 1.3 Update mock exam data in `MockExamData.kt` to supply full exam entries matching the UI mockup (dates, day of week, test types, scores `X/Y`, topics, and subjects).

- [ ] 2. **ViewModel Implementation**
  - [ ] 2.1 Implement `ExamsViewModel` exposing `StateFlow<ExamsUiState>`.
  - [ ] 2.2 Add filtering actions: `onDateSelected`, `onSubjectSelected`, `onTopicSelected`, `onClearFilters`, `openFilterSheet`, `dismissFilterSheet`.

- [ ] 3. **UI Components Construction**
  - [ ] 3.1 Create `ExamFilterChipsRow.kt` for the 3 dropdown chips ("همه تاریخ‌ها", "همه درس‌ها", "همه مباحث").
  - [ ] 3.2 Create `ExamItemCard.kt` with exact matching design:
    - Subject icon badge with subject color.
    - Persian date & day of week.
    - Subject title & topic subtitle.
    - Test type badge ("تستی" / "تشریحی").
    - 3-column stats row: score with chart icon, duration with clock icon, question count with list icon.
  - [ ] 3.3 Create `ExamFilterModalSheet.kt` for interactive filter selection.
  - [ ] 3.4 Update `ExamsScreen.kt` with top header ("آزمون‌های من" + clipboard icon + bell icon), filter row, summary counter ("تعداد کل: ۱۲ آزمون"), `LazyColumn` for exam cards, and floating action button (FAB) for exam creation.

- [ ] 4. **Testing & Verification**
  - [ ] 4.1 Write unit tests in `ExamsViewModelTest.kt` covering filter operations and state updates.
  - [ ] 4.2 Write UI and Robolectric tests in `ExamsScreenTest.kt` checking RTL alignment, filter interactions, card rendering, and FAB navigation.
  - [ ] 4.3 Run `compile_applet` and verify clean build.
