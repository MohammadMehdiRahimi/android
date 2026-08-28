# Tasks: Exam Maker List Screen Redesign (صفحه لیست آزمون‌های من)

- [ ] **1. Data & State Models**
  - [ ] 1.1 Define `ExamCardItem`, `ExamType`, `ExamSubjectTheme`, and `ExamListUiState` in `ExamListModels.kt`.
  - [ ] 1.2 Prepare initial mock dataset reflecting the provided mockup items (ریاضی دهم, فیزیک دهم, شیمی دهم, زیست دهم, etc.).

- [ ] **2. ViewModel & Business Logic**
  - [ ] 2.1 Implement `ExamListViewModel` inheriting `ViewModel` with `MutableStateFlow<ExamListUiState>`.
  - [ ] 2.2 Add filter handlers for dates, subjects, and topics.
  - [ ] 2.3 Expose methods for creating a new exam, viewing exam details, and filtering.

- [ ] **3. UI Components Implementation**
  - [ ] 3.1 Build `ExamListHeader` with "آزمون‌های من" title, purple clipboard icon, and notification bell button.
  - [ ] 3.2 Build `ExamFilterBar` and `DropdownFilterChip` with dropdown menus for dates, subjects, and topics.
  - [ ] 3.3 Build `ExamCountHeader` showing total count formatted with Persian numerals and purple accent.
  - [ ] 3.4 Build `ExamCard` with subject-themed icon badges, type tags ("تستی"/"تشریحی"), Persian date/day, and 3-column stats section with dividers.
  - [ ] 3.5 Assemble `ExamListScreen` with `Scaffold`, `LazyColumn` list of exam cards, and floating action button (FAB) for exam creation.

- [ ] **4. Navigation & Screen Integration**
  - [ ] 4.1 Connect `ExamListScreen` as the default landing view of the Exam Maker tab in `ExamsScreen.kt` / `MainScreen.kt`.
  - [ ] 4.2 Wire the FAB to transition to the exam creation wizard (`BuildExamScreen`).
  - [ ] 4.3 Wire card clicks to open exam review or start screen.

- [ ] **5. Testing & Verification**
  - [ ] 5.1 Create unit test `ExamListViewModelTest` to verify filter logic and state updates.
  - [ ] 5.2 Create Compose UI test for `ExamListScreen` checking test tags, RTL rendering, and click events.
  - [ ] 5.3 Run `compile_applet` and Robolectric unit tests to verify 100% build success.
