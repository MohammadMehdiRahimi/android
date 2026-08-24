# Technical Design: Refinement of Create Study Plan Screen & Catalog API Integration

## 1. Architecture & Layers

### Data Layer (`network/`)
- **API Endpoint:** `GET /study-tasks/me/catalog`
  - Already defined in `ApiService.kt`: `@GET("study-tasks/me/catalog") suspend fun getStudyTaskCatalog(): ApiResponse<StudyTaskCatalogResponseDto>`
  - Response DTO structure:
    ```kotlin
    data class StudyTaskCatalogResponseDto(
        val academicProfile: StudentAcademicProfileDto? = null,
        val books: List<StudyTaskCatalogBookDto> = emptyList(),
    )
    ```
- **Error Handling:** In case of 409 Conflict with `ACADEMIC_PROFILE_INCOMPLETE`, display a helpful state informing the student to complete their academic profile or fallback gracefully.

### Presentation Layer (`ui/features/studyplan/`)

#### 1. `CreateStudyPlanViewModel`
- Calls `api.getStudyTaskCatalog()`.
- Updates `CreateStudyPlanUiState` with live server books, chapters, and topics.
- Automatically selects the first active book, first chapter, and first topics.
- Manages study duration discrete steps (`listOf(15, 30, 60, 90)`) and break duration steps (`listOf(5, 10, 15, 30)`).

#### 2. `CreateStudyPlanScreen.kt` & Composables
- **`CreatePlanTopBar`**:
  - Contains title "ایجاد برنامه مطالعاتی" and subtitle "تنظیم برنامه شخصی‌سازی شده".
  - Right: Single circular back button (`Icons.AutoMirrored.Filled.ArrowBack`) navigating back to `navController.popBackStack()`.
  - Left: Empty spacer/box (no forward button).
- **`ChapterAndTopicsSection`**:
  - Compact topic chips with smaller padding (vertical 4dp, horizontal 8dp) and font size (11sp).
  - Bottom action: Full-width button with outline/surface styling and `Icons.Default.Add` saying "اضافه کردن فصل", which triggers chapter selection or multi-chapter flow.
- **`TimingSection`**:
  - Implements the exact layout from the mockup.
  - Wraps each `Slider` in `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr)` so the slider track progresses intuitively from left (minimum value) to right (maximum value).
  - Uses custom track and thumb aesthetics with purple accent (`#6C5CE7`), step labels (`۱۵`, `۳۰`, `۶۰`, `۹۰` for study time; `۵`, `۱۰`, `۱۵`, `۳۰` for break time).
  - Custom dashed vertical divider between the two columns.
