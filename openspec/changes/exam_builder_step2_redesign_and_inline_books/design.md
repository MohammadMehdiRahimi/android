# Design: Exam Builder Step 2 Redesign & Step 1 Inline Book Selection

## 1. Architecture & State Flow (معماری و جریان داده)

Following Clean Architecture and Unidirectional Data Flow (UDF):
- **Models**:
  - Extend `SelectedBookData` or `BookQuestionConfig`:
    ```kotlin
    data class BookQuestionConfig(
        val bookId: String,
        val bookName: String,
        val chapter: String,
        val topics: List<String>,
        val gradientColors: List<Color>,
        var easyCount: Int = 4,
        var mediumCount: Int = 5,
        var hardCount: Int = 3,
        var veryHardCount: Int = 2
    ) {
        val totalCount: Int get() = easyCount + mediumCount + hardCount + veryHardCount
    }
    ```
- **Step 1 Inline Book Picker Component**:
  - `InlineBookPickerBox`: Replaces the previous `Dialog`/`Modal` with an `AnimatedVisibility` inline card directly below the selected books list.
  - Contains book selector cards/row, chapter dropdown, topic chip selection, and an `افزودن کتاب به آزمون` action button.
- **Step 2 Question Setup Screen (`Step2QuestionSettingsScreen`)**:
  - **Header & Wizard Stepper**: Step 1 checked, Step 2 active, Step 3 upcoming.
  - **Summary Card (`ExamSummaryHeaderCard`)**: 5-column layout displaying Exam Type badge, Grade, Field, Book count, and Question Source.
  - **Book Question Cards List (`BookQuestionSettingsCard`)**:
    - Header: Book title, chapter, cover art gradient/icon, and red delete button (`حذف` + `Icons.Outlined.Delete`).
    - Topics row: `موضوعات انتخابی:` label + chips.
    - 4 difficulty stepper boxes (`آسان`, `متوسط`, `دشوار`, `خیلی دشوار`) with decrement (`-`), count, and increment (`+`) buttons.
    - Total questions label: `جمع سوالات این بخش: X سوال`.
  - **Exam Configuration Card (`ExamSettingsOptionsCard`)**:
    - `نمره منفی` (Negative marking switch + subtext `هر ۴ پاسخ غلط = ۱ پاسخ صحیح منفی`).
    - `چینش تصادفی سوالات` (Random question ordering switch).
  - **Navigation Controls**:
    - `ادامه به مرحله بعد` (Step 3).
    - `بازگشت` (Step 1).

---

## 2. RTL & Design System Constraints
- Strict RTL layout direction (`LayoutDirection.Rtl`).
- Colors matching Shetab theme:
  - Primary Purple: `#6366F1` / `#7C3AED`
  - Accent Red: `#EF4444` for delete actions.
  - Light purple backgrounds: `#F3E8FF`
  - Light gray background for cards: `#FAFAFC` / `#F9FAFB`
- IranSans font typography and Persian numbers formatting (`.toPersianNumber()`).
