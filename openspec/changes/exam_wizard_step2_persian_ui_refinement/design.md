# Design Specification: Exam Wizard Step 2 Persian UI Refinements & Bottom Nav Padding

## 1. Architectural & Layout Strategy

### Clean Architecture & Presentation Layer
All modifications are localized to the Presentation Layer within `com.example.ui.features.exams`:
- `BuildExamScreen.kt`: Handles wizard navigation, step coordinator, and system navigation bar insets (`navigationBarsPadding()`).
- `Step2QuestionSettingsScreen.kt`: Encapsulates text-based summary header, book question cards, difficulty steppers, exam options, and bottom action buttons.
- `Step1ExamStructureScreen.kt`: Updates cancel button styling and bottom clearance to maintain visual consistency across all wizard steps.

---

## 2. UI Component Specifications

### 2.1 Text-Based Top Summary (Persian Design System)
Instead of a nested container card, render a lightweight, inline Persian text badge row:
- **Layout**: `Row` or wrapped flow with `Arrangement.Start` (Right in RTL).
- **Format**:
  - `نوع آزمون: تستی` (with highlighted value in primary/purple tone or bold contrast).
  - `•` or subtle vertical divider.
  - `پایه: دهم`.
  - `•` or subtle vertical divider.
  - `رشته: ریاضی`.
- **Typography**: IranSans / Vazirmatn font family, `fontSize = 12.sp` to `13.sp`, `FontWeight.Medium` for labels and `FontWeight.Bold` for values.

### 2.2 Strict RTL & Book Question Cards (`BookQuestionCard`)
- **Container**: Elevated/Outlined card with `16.dp` rounded corners, `1.dp` border stroke (`#E2E8F0`), and pure white background.
- **Header Row**:
  - **Right (Start)**: Book icon artwork with gradient cover, followed by Book Name (`14.sp`, bold) and Chapter subtitle (`11.5.sp`, secondary text).
  - **Left (End)**: "حذف" (Delete) action with red trash icon and text in a soft red-tinted pill button.
- **Topics Section**:
  - RTL label "موضوعات انتخابی:" followed by Persian pill badges (`Color(0xFFF3E8FF)` background with purple text).
- **Difficulty Steppers Grid**:
  - Horizontal container with 4 difficulty levels arranged strictly RTL:
    1. آسان (Easy)
    2. متوسط (Medium)
    3. دشوار (Hard)
    4. خیلی دشوار (Very Hard)
  - Each stepper has a `+` on the right (start) and `-` on the left (end) in RTL, surrounding Persian numerals (`toPersianNumber()`).

### 2.3 Bottom Action Buttons & Insets Fix
- **Cancel Button ("انصراف")**:
  - `Button` with container color `Color(0xFFEF4444)` (Red) and content color `Color.White`.
  - Rounded corners (`14.dp`), height `46.dp`, bold Persian typography.
- **Back Button ("بازگشت")**:
  - Styled with secondary outline or text button with high contrast.
- **Navigation Bar Clearance**:
  - Apply `Modifier.navigationBarsPadding()` to the bottom container in `BuildExamScreen.kt` and add a `Spacer(modifier = Modifier.height(24.dp))` to ensure buttons are clearly visible above Android gesture handles and virtual navigation keys.

---

## 3. Data Flow & State Management
No changes to the data models or domain entities. `BookQuestionConfig`, `SelectedExamBook`, and `ExamsViewModel` remain unchanged and state flows unidirectionally via existing lambdas (`onConfigChange`, `onRemoveBook`, `onNextStep`, `onPrevStep`).

---

## 4. Testing Plan
- **Unit & Robolectric Tests**:
  - Verify Step 2 UI renders the text-based summary with correct attributes.
  - Verify Cancel button click triggers cancellation or navigation back.
  - Verify Stepper increments and decrements correctly update configuration.
