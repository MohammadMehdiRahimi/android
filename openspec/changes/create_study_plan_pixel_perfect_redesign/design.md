# Design: Create Study Plan Pixel-Perfect Redesign (صفحه ایجاد برنامه مطالعاتی)

## 1. Architectural Overview
This feature redesigns the `CreateStudyPlanScreen` UI adhering to **Clean Architecture**, **Material Design 3 (M3)**, and **Unidirectional Data Flow (UDF)**.

```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                      │
│                                                             │
│  CreateStudyPlanScreen (Compose UI)                         │
│  ├── Header (Back button, Title, Subtitle - No avatar)      │
│  ├── WeekDateSelectorRow (Horizontal Persian week days)     │
│  ├── DaySummaryCard (Total time, Session count, Circular %) │
│  ├── SessionCardsList (LazyColumn items with status/badges) │
│  ├── AddSessionOutlineButton ("+ افزودن جلسه")              │
│  ├── AddSessionModalSheet (Catalog / Custom session form)   │
│  └── StickyBottomActionsBar ("ذخیره برنامه روز", "کپی")     │
│                                                             │
│  CreateStudyPlanViewModel                                   │
│  ├── StateFlow<CreateStudyPlanUiState>                      │
│  └── SharedFlow<CreateStudyPlanEvent>                       │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│                        Domain Layer                         │
│  - JalaliDate & DateTransformer                             │
│  - GetStudyCatalogUseCase                                   │
│  - CreateManualStudyTaskUseCase                             │
│  - DayStudyPlanSession Models                               │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│                    Data & Network Layer                     │
│  - StudyTaskRepository / StudyTaskRepositoryImpl            │
│  - ApiClient & ApiService (/study-task/manual)              │
│  - TokenManager (User profile name extraction)              │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. UI State & Domain Models

### 2.1 Session Item Model
```kotlin
data class StudySessionUiModel(
    val id: String = UUID.randomUUID().toString(),
    val subjectTitle: String,
    val chapterTopic: String,
    val startTime: String, // e.g., "08:00"
    val durationMinutes: Int, // e.g., 90
    val isCompleted: Boolean = false,
    val isNext: Boolean = false,
    val subjectCategory: SubjectCategory = SubjectCategory.BIOLOGY,
    val customGrade: String? = null,
    val topicIds: List<String> = emptyList(),
)

enum class SubjectCategory(
    val title: String,
    val iconTint: Color,
    val containerBg: Color,
    val iconType: SubjectIconType,
) {
    BIOLOGY("زیست شناسی", Color(0xFF2E7D32), Color(0xFFE8F5E9), SubjectIconType.LEAF),
    MATH("ریاضی", Color(0xFFE65100), Color(0xFFFFF3E0), SubjectIconType.MATH),
    CHEMISTRY("شیمی", Color(0xFFC2185B), Color(0xFFFFEBEE), SubjectIconType.FLASK),
    PHYSICS("فیزیک", Color(0xFF1565C0), Color(0xFFE3F2FD), SubjectIconType.ATOM),
    LITERATURE("ادبیات فارسی", Color(0xFF6A1B9A), Color(0xFFEDE7F6), SubjectIconType.BOOK),
    REVIEW("مرور و تست", Color(0xFF0277BD), Color(0xFFE1F5FE), SubjectIconType.DOCUMENT),
    GENERAL("عمومی", Color(0xFF5B42F3), Color(0xFFEDE7F6), SubjectIconType.BOOK),
}

enum class SubjectIconType {
    LEAF, MATH, FLASK, ATOM, BOOK, DOCUMENT
}
```

### 2.2 Week Day Item Model
```kotlin
data class WeekDayItem(
    val dayOfWeekName: String, // e.g., "دوشنبه"
    val dayOfMonth: Int,        // e.g., 14
    val monthName: String,      // e.g., "اردیبهشت"
    val date: JalaliDate,
    val isSelected: Boolean,
)
```

### 2.3 Extended `CreateStudyPlanUiState`
```kotlin
data class CreateStudyPlanUiState(
    val studentName: String = "علی محمدی",
    val selectedDate: JalaliDate = DateTransformer.getTodayJalali(),
    val weekDays: List<WeekDayItem> = emptyList(),
    val sessions: List<StudySessionUiModel> = emptyList(),
    val totalStudyMinutes: Int = 0,
    val completedSessionsCount: Int = 0,
    val progressPercentage: Int = 0,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val isAddSessionSheetVisible: Boolean = false,
    val editingSession: StudySessionUiModel? = null,
    val userMajor: String = "EXPERIMENTAL",
    val userMajorName: String = "رشته تجربی",
    val subjectsByGrade: Map<String, List<SubjectVisualItem>> = emptyMap(),
    val errorMessage: String? = null,
    val successMessage: String? = null,
)
```

---

## 3. Visual Styling & Color Tokens

### 3.1 Color Palette
- **Canvas Background:** `#F8F9FD` (Crisp light grayish blue)
- **Card Background:** `#FFFFFF` with `4.dp` to `8.dp` soft elevation or subtle border `#ECEFF5`
- **Primary Brand Purple:** `#6C47FF` (Main CTA, checkboxes, accents)
- **Gradient Selected Day:** LinearGradient(0° to 180°, `#7B52FF`, `#5438DC`)
- **Success Green:** Text `#10B981`, Background `#E8F5E9`
- **Next Session Border:** `#7C4DFF` with `1.5.dp` stroke
- **Text Primary:** `#1F2937` (Dark slate)
- **Text Secondary:** `#6B7280` (Muted gray)
- **Text Purple:** `#6C47FF`

### 3.2 Typography & Persian Formatting
- Headers: `IranSansFontFamily` / `VazirmatnFontFamily` (Bold, 18.sp - 20.sp)
- Subtitles & Labels: Regular/Medium, 12.sp - 14.sp
- Numbers: Rendered in Persian numerals using `.toPersianNumber()`
- Time: Formatted as `HH:mm` in Persian (e.g., `۰۸:۰۰`, `۱۰:۰۰`, `۱۳:۳۰`)
- Durations: Formatted as `X دقیقه` in Persian

---

## 4. Screen Layout & Component Structure

1. **`CreatePlanTopBar`**:
   - Back button: Elevated circular surface with `Icons.AutoMirrored.Filled.ArrowBack`
   - Centered column:
     - Title: `برنامه‌ریزی برای $studentName` (Bold, 18.sp)
     - Subtitle: `برنامه درسی و مطالعه` (13.sp, `#6B7280`)
   - *Avatar excluded per instructions.*

2. **`WeekDaysSelectorCard`**:
   - Card container with horizontal scroll / row.
   - Previous and next week arrow buttons.
   - Purple calendar icon at the start.
   - Day pill/cards: selected day rendered as a rounded rectangle with purple gradient, day name, large date number, month name, and white dot indicator at bottom.

3. **`DaySummaryMetricsCard`**:
   - Elevated white card (`shape = RoundedCornerShape(20.dp)`).
   - 3 columns with vertical dividers:
     - Right: Clock icon in purple circle + «۶:۳۰ ساعت» + «کل زمان مطالعه»
     - Center: Bulleted list icon in purple circle + «۵ جلسه» + «تعداد جلسات»
     - Left: Circular progress indicator (purple track `#E0E7FF`, progress `#6C47FF`) + «۳۰٪» + «پیشرفت روز»

4. **`StudySessionCard`**:
   - Elevated white card (`shape = RoundedCornerShape(16.dp)`).
   - If `isNext == true`: `border = BorderStroke(1.5.dp, Color(0xFF7C4DFF))`.
   - Start: Rounded Checkbox. Tapping toggles completion and updates daily progress.
   - Next: Subject Icon container (44x44dp, rounded 12dp, colored background with vector icon).
   - Middle: Subject title (bold 15sp) and chapter/topic (12sp `#6B7280`).
   - Badges:
     - If completed: «انجام شده» (Green badge)
     - If next: «بعدی» (Purple badge)
   - End: Start time (e.g. `۰۸:۰۰`) and duration (e.g. `۹۰ دقیقه`).
   - Far end: Overflow menu icon (`MoreVert`) with Edit and Delete options.

5. **`AddSessionOutlineButton`**:
   - Outlined rounded button (`shape = RoundedCornerShape(14.dp)`, border `#6C47FF`, height 50dp) with «+ افزودن جلسه».
   - Tapping opens `AddSessionModalSheet` where the user can pick from subjects/topics or enter custom session details.

6. **`StickyBottomActionsBar`**:
   - Row with 2 action buttons:
     - **Primary:** «ذخیره برنامه روز ✓» (Filled purple button, 52dp height, bold, rounded 14dp).
     - **Secondary:** «کپی از روز قبل» (Outlined button, copy icon, 52dp height, rounded 14dp).

---

## 5. Testability & Semantics
- `Modifier.testTag("create_plan_back_button")`
- `Modifier.testTag("create_plan_title")`
- `Modifier.testTag("week_day_item_$date")`
- `Modifier.testTag("day_summary_card")`
- `Modifier.testTag("session_item_card_$id")`
- `Modifier.testTag("session_checkbox_$id")`
- `Modifier.testTag("add_session_button")`
- `Modifier.testTag("save_day_plan_button")`
- `Modifier.testTag("copy_previous_day_button")`
