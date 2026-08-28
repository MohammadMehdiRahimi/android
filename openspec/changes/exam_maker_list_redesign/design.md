# Design: Exam Maker List Screen Redesign (صفحه لیست آزمون‌های من)

## 1. Architecture & State Management (UDF)
The screen adheres strictly to **Clean Architecture** and **Unidirectional Data Flow (UDF)**.

### State Hierarchy
```kotlin
enum class ExamType(val title: String) {
    MULTIPLE_CHOICE("تستی"),
    DESCRIPTIVE("تشریحی")
}

enum class ExamSubjectTheme(
    val title: String,
    val iconBgColor: Long,
    val iconTint: Long
) {
    MATH("ریاضی", 0xFFF3E8FF, 0xFF8B5CF6),
    PHYSICS("فیزیک", 0xFFDCFCE7, 0xFF10B981),
    CHEMISTRY("شیمی", 0xFFFEF3C7, 0xFFF59E0B),
    BIOLOGY("زیست", 0xFFE0F2FE, 0xFF0284C7),
    OTHER("سایر", 0xFFF1F5F9, 0xFF64748B)
}

data class ExamCardItem(
    val id: String,
    val subjectName: String,
    val grade: String,
    val topic: String,
    val examType: ExamType,
    val date: String,
    val dayOfWeek: String,
    val scoreAchieved: String,
    val scoreTotal: String,
    val durationMinutes: Int,
    val questionCount: Int,
    val theme: ExamSubjectTheme
)

data class ExamListUiState(
    val exams: List<ExamCardItem> = emptyList(),
    val filteredExams: List<ExamCardItem> = emptyList(),
    val totalCount: Int = 0,
    val selectedDateFilter: String = "همه تاریخ‌ها",
    val selectedSubjectFilter: String = "همه درس‌ها",
    val selectedTopicFilter: String = "همه مباحث",
    val isDateDropdownOpen: Boolean = false,
    val isSubjectDropdownOpen: Boolean = false,
    val isTopicDropdownOpen: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
```

## 2. Visual & Layout Specifications (RTL First)

### Screen Canvas & Spacing
- Background: `#F8FAFC` to `#FFFFFF` soft gradient / neutral background.
- Padding: 16.dp horizontal screen margin, 12.dp vertical item spacing.

### Header Component (`ExamListHeader`)
- **Start (Right in RTL)**:
  - Title: "آزمون‌های من" (`20.sp`, `FontWeight.Bold`, `AnalyzerNavy` / `#0F172A`).
  - Icon: Clipboard vector in `#8B5CF6` (size: `24.dp`).
- **End (Left in RTL)**:
  - Notification icon in circular / rounded-rect surface with border and subtle shadow.

### Filter Dropdowns Row (`ExamFilterBar`)
- 3 filter pills laid out in a horizontal `Row(horizontalArrangement = Arrangement.spacedBy(8.dp))`:
  - `DropdownFilterChip(label = selectedDateFilter, icon = Icons.Outlined.CalendarToday)`
  - `DropdownFilterChip(label = selectedSubjectFilter, icon = Icons.Outlined.MenuBook)`
  - `DropdownFilterChip(label = selectedTopicFilter, icon = Icons.Outlined.Layers)`
- Styling: Rounded corner `14.dp`, white background, `1.dp` border `#E2E8F0`, dark gray text `#334155`, chevron arrow `#94A3B8`.

### Counter Header
- Row displaying: `Text("تعداد کل: ")` + `Text("$totalCount آزمون", color = #8B5CF6, fontWeight = Bold)`.

### Exam Card Item (`ExamCard`)
- Rounded card with `18.dp` radius, white surface `#FFFFFF`, soft shadow (`elevation = 1.dp`), subtle border (`1.dp #F1F5F9`).
- **Top Section**:
  - Right in RTL:
    - Subject icon in themed rounded container (`44.dp` x `44.dp`, rounded `14.dp`).
    - Chevron left icon (`Icons.AutoMirrored.Filled.KeyboardArrowLeft`) next to it.
    - Type pill tag: "تستی" (`#EDE9FE` background, `#7C3AED` text) or "تشریحی" (`#DCFCE7` background, `#16A34A` text).
    - Subject title: e.g. "ریاضی دهم" (`15.sp`, `Bold`, `#0F172A`).
    - Topic subtitle: e.g. "معادله و نامعادله" (`12.sp`, `Normal`, `#64748B`).
  - Left in RTL:
    - Date: e.g. "۱۴۰۳/۰۳/۲۵" (`12.sp`, `SemiBold`, `#1E293B`).
    - Weekday: e.g. "جمعه" (`11.sp`, `Normal`, `#94A3B8`).
- **Bottom Section (Stats Grid)**:
  - 3 columns with vertical dividers `#F1F5F9`:
    - **Right (Score)**: "نمره" label (`10.5.sp`, `#94A3B8`), value e.g. "۱۸/۳۰" (`13.sp`, `Bold`, `#6366F1`), icon `Icons.Outlined.BarChart`.
    - **Middle (Time)**: "زمان" label (`10.5.sp`, `#94A3B8`), value e.g. "۴۵ دقیقه" (`12.sp`, `Bold`, `#1E293B`), icon `Icons.Outlined.AccessTime`.
    - **Left (Question count)**: "تعداد تست" label (`10.5.sp`, `#94A3B8`), value e.g. "۳۰" (`12.sp`, `Bold`, `#1E293B`), icon `Icons.Outlined.FormatListBulleted`.

### Floating Action Button (FAB)
- Floating action button pinned to bottom-start with gradient/purple `#8B5CF6`, white plus icon `+`, shadow elevation `6.dp`.
- `Modifier.testTag("create_exam_fab")`

## 3. Localization & RTL
- Layout Direction: Strict RTL.
- Persian Digits: Formatted via utility for Persian numerals (`۰۱۲۳۴۵۶۷۸۹`).
- Strings externalized in `res/values/strings.xml`.
