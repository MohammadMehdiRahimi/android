# Design: Exam Maker List Screen Redesign (طراحی صفحه لیست آزمون‌های من)

## 1. Architecture & Component Hierarchy

The feature adheres to **Clean Architecture** and **MVVM** with **Unidirectional Data Flow (UDF)**.

```
com.example.ui.features.exams/
├── ExamsScreen.kt                 // Main Compose Screen rendering header, filter chips, exam list, FAB
├── ExamsViewModel.kt              // State holder managing exam list, selected filters, filtering logic
├── ExamsUiState.kt                // Immutable data class for the UI state
├── components/
│   ├── ExamFilterChipsRow.kt      // Dropdown filter chips ("همه تاریخ‌ها", "همه درس‌ها", "همه مباحث")
│   ├── ExamItemCard.kt            // Individual exam card adhering strictly to the UI design
│   └── ExamFilterModalSheet.kt    // Bottom sheet dialog for choosing filter options
└── ...
```

---

## 2. UI & Layout Specifications (Adhering strictly to `exam-list-page.png`)

### 2.1 Screen Layout & RTL Support
- Native RTL layout using `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl)`.
- Main Container: `Box(modifier = Modifier.fillMaxSize())` with `Scaffold` or `Column` containing top bar, filter row, count label, `LazyColumn` for exam items, and floating `FloatingActionButton` placed at bottom-start (aligned left in RTL).

### 2.2 Top Header
- **Title & Icon**: Row with `Text("آزمون‌های من")` (fontSize = 20.sp, fontWeight = Bold, color = primary text) and a purple checklist/exam icon.
- **Action Button**: Outlined notification bell icon button (`Icons.Outlined.Notifications` or circular container) with ripple feedback.

### 2.3 Filter Chips Bar (`ExamFilterChipsRow`)
- 3 filter chips arranged horizontally with spaced by 8.dp:
  1. **Date Filter**: Text ("همه تاریخ‌ها" / Selected Date), leading/trailing calendar icon (`Icons.Outlined.DateRange`), dropdown chevron (`Icons.Default.KeyboardArrowDown`).
  2. **Subject Filter**: Text ("همه درس‌ها" / Selected Subject), book icon (`Icons.Outlined.MenuBook`), dropdown chevron (`Icons.Default.KeyboardArrowDown`).
  3. **Topic Filter**: Text ("همه مباحث" / Selected Topic), layers icon (`Icons.Outlined.Layers`), dropdown chevron (`Icons.Default.KeyboardArrowDown`).
- Clean white card styling with rounded corners (16.dp), subtle border (`0.8.dp`), and 48dp minimum interactive touch height.

### 2.4 Summary Label
- A `Row` under filters with:
  - `"تعداد کل: "` (Color: secondary text / slate, fontSize = 13.sp)
  - `"$count آزمون"` (Color: primary accent purple `#6C5CE7` or `colors.accentMain`, fontWeight = ExtraBold, fontSize = 14.sp).

### 2.5 Exam Card Item (`ExamItemCard`)
Card with `RoundedCornerShape(20.dp)`, surface color `colors.cardBg` (white), light border (`1.dp` with alpha `0.06f`), and `2.dp` elevation.
- **Top Row**:
  - **Start (Right in RTL)**:
    - Subject Icon Avatar: 44.dp rounded container (14.dp shape) with subject theme tint (Math = Purple, Physics = Green, Chemistry = Orange, Biology = Light Blue) and checklist icon.
    - Title Column:
      - Row with Subject Title (e.g. "ریاضی دهم") + Test Type Badge:
        - "تستی": Background `Color(0xFFF3E8FF)`, Text `Color(0xFF7C3AED)`, Shape 10.dp.
        - "تشریحی": Background `Color(0xFFE6F7ED)`, Text `Color(0xFF10B981)`, Shape 10.dp.
      - Topic Title (e.g. "معادله و نامعادله", secondary text, 12.sp).
  - **End (Left in RTL)**:
    - Persian Date string (e.g. "۱۴۰۳/۰۳/۲۵", bold 12.sp).
    - Day of week (e.g. "جمعه", secondary text 11.sp).
  - **Trailing Indicator**: Subtle chevron (`Icons.AutoMirrored.Filled.KeyboardArrowLeft`).
- **Divider & Bottom 3-Column Metrics**:
  - Thin horizontal divider line (`0.5.dp`).
  - Row with 3 equal/spaced columns:
    1. **نمره (Score)**: Top label "نمره", bottom value e.g. "۱۸/۳۰" in purple accent alongside a bar chart icon (`Icons.Outlined.BarChart` or `Icons.Default.BarChart`).
    2. **زمان (Duration)**: Top label "زمان", bottom value e.g. "۴۵ دقیقه" with schedule clock icon (`Icons.Outlined.Schedule`).
    3. **تعداد تست (Question Count)**: Top label "تعداد تست", bottom value e.g. "۳۰" with list icon (`Icons.Outlined.FormatListBulleted`).

### 2.6 Floating Action Button (FAB)
- Positioned in bottom-start corner above bottom navigation (or Scaffold `FloatingActionButtonPosition.Start`).
- Circular shape (56.dp) with rich purple gradient/color, white `+` (`Icons.Default.Add`) icon.
- `Modifier.testTag("create_exam_fab")` navigating to `"build_exam"`.

---

## 3. Data Models & State Management

### 3.1 Exam Item Model (`ExamListItem`)
```kotlin
enum class ExamType(val title: String) {
    MULTIPLE_CHOICE("تستی"),
    DESCRIPTIVE("تشریحی")
}

data class ExamListItem(
    val id: String,
    val subject: String,
    val topic: String,
    val date: String,
    val dayOfWeek: String,
    val examType: ExamType,
    val score: String, // e.g. "18/30"
    val scorePercentage: Int,
    val durationMinutes: Int,
    val questionCount: Int,
    val themeColor: Color
)
```

### 3.2 UI State (`ExamsUiState`)
```kotlin
data class ExamsUiState(
    val isLoading: Boolean = false,
    val allExams: List<ExamListItem> = emptyList(),
    val filteredExams: List<ExamListItem> = emptyList(),
    val availableDates: List<String> = emptyList(),
    val availableSubjects: List<String> = emptyList(),
    val availableTopics: List<String> = emptyList(),
    val selectedDate: String? = null,
    val selectedSubject: String? = null,
    val selectedTopic: String? = null,
    val activeFilterType: FilterType? = null // For displaying filter modal
)
```

---

## 4. Test Strategy
- **`ExamsViewModelTest`**: Test initial state loading, filtering by subject/date/topic, resetting filters, and verifying count calculations.
- **`ExamsScreenTest`**: Robolectric Compose test ensuring header, filter chips, summary counter, exam cards, and FAB exist and are clickable.
