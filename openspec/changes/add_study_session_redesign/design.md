# Design: Add Study Session Screen Redesign (طراحی فنی و بصری صفحه افزودن جلسه)

## 1. Architectural Strategy & Clean Architecture
In accordance with `openspec/project.md`, the Add Study Session module adheres to:
*   **Feature-First Presentation Layer:** `com.example.ui.features.studyplan`
*   **Unidirectional Data Flow (UDF):** The screen observes an immutable state flow (`StateFlow<AddStudySessionUiState>`) exposed by the ViewModel.
*   **Domain & Data Integration:** Integrates with `StudyTaskRepository` and `CreateManualStudyTaskUseCase` to commit newly configured study sessions to local Room database and remote API endpoints when authenticated.
*   **Clean Separation:** Separates UI rendering from business logic, ensuring components are modular, previewable, and testable without side effects.

---

## 2. State Modeling & Data Structures

```kotlin
data class StudySubjectItem(
    val id: String,
    val title: String,
    val isSelected: Boolean = false,
)

data class StudyTopicItem(
    val id: String,
    val title: String,
    val isSelected: Boolean = false,
)

data class StudyChapterItem(
    val id: String,
    val title: String,
    val chapterNumber: Int,
    val topics: List<StudyTopicItem> = emptyList(),
    val selectedTopicId: String? = null,
)

data class AddStudySessionUiState(
    val selectedGrade: String = "پایه دوازدهم",
    val availableGrades: List<String> = listOf("پایه دهم", "پایه یازدهم", "پایه دوازدهم", "کنکور"),
    val isGradeDropdownOpen: Boolean = false,
    
    val subjects: List<StudySubjectItem> = listOf(
        StudySubjectItem("econ", "اقتصاد", isSelected = true),
        StudySubjectItem("hist", "تاریخ", isSelected = false),
        StudySubjectItem("soc", "جامعه شناسی", isSelected = false),
        StudySubjectItem("geo", "جغرافیا", isSelected = false),
    ),
    
    val chapters: List<StudyChapterItem> = listOf(
        StudyChapterItem(
            id = "chap_1",
            title = "۱: کسب‌وکار و کارآفرینی",
            chapterNumber = 1,
            topics = listOf(
                StudyTopicItem("top_1", "موفقیت و شکست کسب‌وکارها", isSelected = true),
                StudyTopicItem("top_2", "کارآفرینی و نقش", isSelected = false),
            ),
            selectedTopicId = "top_1",
        )
    ),
    
    val cycleCount: Int = 3, // ۳ دوره
    val studyDurationMinutes: Int = 45, // ۴۵ دقیقه
    val restDurationMinutes: Int = 15, // ۱۵ دقیقه
    
    val isSubmitting: Boolean = false,
    val submissionSuccess: Boolean = false,
    val errorMessage: String? = null,
)
```

---

## 3. UI Design Specifications & Color Palette

### Colors & Brush Values
*   **Primary Purple:** `Color(0xFF5E43E2)` / `Color(0xFF6B4EE8)`
*   **Purple Accent / Track:** `Color(0xFF7556F6)`
*   **Light Lavender Background:** `Color(0xFFF7F5FC)`
*   **Light Card Border:** `Color(0xFFECE8F6)`
*   **Info Box Background:** `Color(0xFFF5F2FF)`
*   **Info Box Border:** `Color(0xFFE4DBFB)`
*   **Primary Navy Text:** `Color(0xFF1E1548)`
*   **Secondary Slate Text:** `Color(0xFF78788D)`
*   **Inactive Slider Track:** `Color(0xFFE5E5EB)`
*   **White Container:** `Color(0xFFFFFFFF)`

### Layout & Sizing
*   **Main Container Card:**
    *   Outer margins: `horizontal = 16.dp`, `vertical = 8.dp`
    *   Corner radius: `RoundedCornerShape(32.dp)`
    *   Internal padding: `20.dp`
    *   Subtle shadow: `shadow(elevation = 3.dp, shape = RoundedCornerShape(32.dp), spotColor = Color(0x1A6B4EE8))`
*   **Top Bar:**
    *   Circle button size: `46.dp x 46.dp`, shape `CircleShape`
    *   Circle button border: `BorderStroke(1.dp, Color(0xFFEFEBF8))`
    *   Title: `19.sp`, bold, IranSans / Vazirmatn, `Color(0xFF1E1548)`
    *   Subtitle: `12.5.sp`, regular, `Color(0xFF78788D)`
*   **Nested Chapter Card:**
    *   Shape: `RoundedCornerShape(20.dp)`
    *   Background: `Color(0xFFF9F8FD)`
    *   Border: `BorderStroke(1.dp, Color(0xFFECE9F7))`
    *   Dropdown field: `Color.White`, `RoundedCornerShape(14.dp)`, border `Color(0xFFE2DEF2)`
    *   Radio options: `Color.White`, `RoundedCornerShape(14.dp)`, border `Color(0xFFE8E5F3)`, height `44.dp`
*   **Schedule Counter & Sliders:**
    *   Counter container: `shape = RoundedCornerShape(14.dp)`, `background = Color(0xFFF3F1FA)`
    *   Discrete Sliders:
        *   Study duration steps: `[15, 30, 45, 60, 90]`
        *   Rest duration steps: `[5, 10, 15, 20, 30]`
        *   Interactive touch drag & tap gesture handling with animated thumb offset and Persian tick labels.
*   **Bottom Buttons:**
    *   Primary "ثبت جلسه": `Modifier.weight(0.65f)`, height `52.dp`, shape `RoundedCornerShape(16.dp)`, purple gradient background, white text + check icon.
    *   Secondary "انصراف": `Modifier.weight(0.35f)`, height `52.dp`, shape `RoundedCornerShape(16.dp)`, white background, purple outline + icon.

---

## 4. Persian Localization & RTL Compliance
*   **Root Direction:** Entire layout rendered under `LocalLayoutDirection provides LayoutDirection.Rtl`.
*   **Persian Digit Formatting:** Utilizes `.toPersianDigits()` extension function for all numbers (cycle count: "۳ دوره", durations: "۴۵ د", "۱۵ د", slider ticks: "۵، ۱۰، ۱۵، ۲۰، ۳۰، ۹۰").
*   **Icon Direction:** Uses `Icons.AutoMirrored.Filled.ArrowBack` for the back button so it points correctly in RTL.
*   **No Left/Right:** Strict usage of `start` and `end` in padding and alignment.

---

## 5. UI Component Hierarchy
```text
Scaffold (containerColor = Color(0xFFFAF9FD))
└── Box / Column (verticalScroll)
    ├── AddStudySessionHeader
    │   ├── CircularBackButton (Left)
    │   ├── TitlesColumn (Center: Title + Subtitle)
    │   └── CircularBookIcon (Right)
    └── MainContentCard (White, 32.dp rounded)
        ├── CourseGradeSection (Title: درس و کتاب ۱ + Dropdown: پایه دوازدهم)
        ├── SubjectChipsRow (اقتصاد [Selected], تاریخ, جامعه شناسی, جغرافیا)
        ├── ChapterBox (فصل و مباحث ۱ + Dropdown + Radio Topics)
        ├── AddChapterButton (+ اضافه کردن فصل)
        ├── HorizontalDivider
        ├── ScheduleHeaderRow (زمان‌بندی و دوره‌های مطالعه + Counter: ۳ دوره)
        ├── StudyRestSlidersRow
        │   ├── RestSliderColumn (زمان استراحت, ۱۵ د, ۵-۳۰ slider)
        │   └── StudySliderColumn (زمان مطالعه, ۴۵ د, ۱۵-۹۰ slider)
        ├── InformationNoticeBox (با ثبت این جلسه، برنامه درسی...)
        └── BottomActionButtonsRow (انصراف + ثبت جلسه)
```

---

## 6. Testing Strategy
*   **Unit Tests (`CreateStudyPlanViewModelTest`):**
    *   Verify cycle counter increment/decrement constraints.
    *   Verify subject and topic selection updates state.
    *   Verify study and rest slider value transitions.
    *   Verify submission trigger and error handling.
*   **UI Tests (`CreateStudyPlanScreenTest`):**
    *   Verify exact presence of header texts, chips, dropdowns, and buttons.
    *   Verify test tags (`testTag("add_session_submit_button")`, `testTag("add_session_cancel_button")`, etc.).
    *   Verify RTL direction and Persian numeral labels rendering.
