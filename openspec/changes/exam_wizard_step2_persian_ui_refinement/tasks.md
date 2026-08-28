# Tasks: Exam Wizard Step 2 Persian UI Refinements & Bottom Nav Padding

- [ ] **1. Presentation / UI Layer Refinements**
  - [ ] **1.1. Replace Summary Card with Text-Based Summary in Step 2**:
    - Update `Step2QuestionSettingsScreen.kt` to replace the top `Card` with a clean Persian design text row: `نوع آزمون: [تستی]   پایه: [دهم]   رشته: [ریاضی]`.
  - [ ] **1.2. Refine Book Question Cards with Persian Design System & RTL**:
    - Ensure strict RTL orientation across `BookQuestionCard` (Cover & Book name on start/right, Delete action on end/left).
    - Refine card borders, spacing, typography, topic pills, and difficulty steppers (`آسان`, `متوسط`, `دشوار`, `خیلی دشوار`).
  - [ ] **1.3. Style "انصراف" (Cancel) Button & Fix Bottom Insets**:
    - Style the Cancel button with a red background (`#EF4444`) and white text.
    - Add `navigationBarsPadding()` and increased bottom clearance to `BuildExamScreen.kt` and `Step2QuestionSettingsScreen.kt` so buttons are lifted above the phone's system navigation buttons.
- [ ] **2. Verification & Testing**
  - [ ] Update / run Robolectric and Compose UI tests in `BuildExamWizardTest.kt` or `ExamsScreenTest.kt`.
  - [ ] Verify compilation with `compile_applet`.
