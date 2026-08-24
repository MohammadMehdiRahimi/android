# Feature Design: Create Custom Study Plan Screen (ایجاد برنامه مطالعاتی)

## 1. Architectural Approach
The feature will reside within `ui/features/studyplan/` following Feature-First Clean Architecture and MVVM patterns.

### Navigation Flow:
- Route: `create_study_plan`
- Source:
  - Floating Action Button '+' in `StudyPlanScreen`
  - "افزودن تسک" button in Empty State
  - Dashboard or planner shortcuts
- Destination upon submission or back: Returns to `StudyPlanScreen` with refreshed data.

## 2. State & Data Modeling

### CreateStudyPlanUiState:
```kotlin
data class CreateStudyPlanUiState(
    val selectedGrade: String = "GRADE_12",
    val selectedGradeName: String = "پایه دوازدهم",
    val selectedBookId: String = "biology_12",
    val selectedBookName: String = "زیست‌شناسی",
    val selectedBookField: String = "رشته تجربی",
    val selectedChapterId: String = "chap_1",
    val selectedChapterName: String = "فصل اول: زیست‌شناسی سلولی",
    val selectedTopicIds: Set<String> = setOf("top_1", "top_2", "top_summary"),
    val periodCount: Int = 3,
    val useDefaultTiming: Boolean = true,
    val studyDurationMinutes: Int = 45,
    val breakDurationMinutes: Int = 15,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
) {
    val totalEstimatedMinutes: Int
        get() = periodCount * (studyDurationMinutes + breakDurationMinutes)
        
    val formattedTotalTime: String
        get() {
            val hours = totalEstimatedMinutes / 60
            val minutes = totalEstimatedMinutes % 60
            return if (hours > 0 && minutes > 0) {
                "${hours.toPersianNumber()} ساعت و ${minutes.toPersianNumber()} دقیقه"
            } else if (hours > 0) {
                "${hours.toPersianNumber()} ساعت"
            } else {
                "${minutes.toPersianNumber()} دقیقه"
            }
        }
}
```

## 3. UI Component Hierarchy & Design Specifications

1. **Top Bar / Header (`CreatePlanTopBar`):**
   - Circle action button at start (back arrow `Icons.AutoMirrored.Filled.ArrowBack` or `Icons.AutoMirrored.Filled.ArrowForward` depending on RTL semantics).
   - Centered column: Title "ایجاد برنامه مطالعاتی" (18sp, Bold, Navy), Subtitle "برنامه اختصاصی خود را در چند گام بسازید" (12sp, Muted).
   - Circle action button at end (Arrow button pointing to the step direction).

2. **Section 1: Grade & Book Selection (`GradeAndBookSection`):**
   - Header with book icon `Icons.AutoMirrored.Outlined.MenuBook` and title "پایه و کتاب".
   - Grade selector dropdown button with arrow indicator (e.g., "پایه دوازدهم").
   - Horizontal `LazyRow` or grid of subject cards:
     - زیست‌شناسی (Green theme, leaf icon, "رشته تجربی", selected checkmark in green circle)
     - فیزیک (Blue theme, atom icon, "رشته تجربی")
     - شیمی (Orange/Amber theme, flask icon, "رشته تجربی")
     - ریاضی (Pink/Rose theme, calculator icon, "رشته تجربی")
   - Indicator dots for carousel pagination.

3. **Section 2: Chapter & Topics (`ChapterAndTopicsSection`):**
   - Header with list icon and title "فصل و مباحث", expandable chevron.
   - Chapter header row with title "فصل اول: زیست‌شناسی سلولی" and green check circle.
   - Flow row of selectable topic chips with checkboxes/states:
     - گفتار ۱ (Selected, green border & green check)
     - گفتار ۲ (Selected, green border & green check)
     - گفتار ۳ (Unselected, purple/neutral border & empty circle)
     - جمع‌بندی (Selected, purple border & check)
   - Badge counter footer: "۳ مورد انتخاب شده" with document icon.

4. **Section 3: Study Periods (`StudyPeriodsSection`):**
   - Header with sync/cycle icon and title "دوره‌های مطالعه".
   - Counter component: Rounded pill with purple '-' button, large bold count (e.g., "۳ دوره"), and purple '+' button.
   - Side note: Info icon with "هر دوره شامل مطالعه و استراحت".

5. **Section 4: Timing & Sliders (`TimingSection`):**
   - Header with clock icon and title "زمان‌بندی", toggle switch for "استفاده از زمان‌بندی پیش‌فرض".
   - Two side-by-side or stacked slider cards:
     - **زمان مطالعه:** Book icon, duration label (e.g. "۴۵ دقیقه"), custom stepped slider with tick marks at 15, 30, 45, 60, 90.
     - **استراحت:** Coffee cup icon, duration label (e.g. "۱۵ دقیقه"), custom stepped slider with tick marks at 5, 10, 15, 30.

6. **Section 5: Summary Matrix Card (`PlanSummaryMatrixCard`):**
   - Header: Sparkle icon with "خلاصه برنامه شما".
   - 4-column divided grid:
     1. **کتاب:** Icon book, "زیست‌شناسی", "پایه دوازدهم".
     2. **فصل و مباحث:** Icon list, "فصل اول", "۳ مورد".
     3. **دوره‌ها:** Icon repeat, "۳ دوره".
     4. **زمان تقریبی کل:** Icon clock, "۳ ساعت", "تقریباً".

7. **Section 6: Save & Submit Button (`SavePlanButton`):**
   - Full width elevated button with vivid purple background (`Color(0xFF6C5CE7)`).
   - Text "ثبت و ذخیره برنامه" with sparkle icon `Icons.Default.AutoAwesome`.

## 4. User Profile Header Update in `StudyPlanScreen`:
- Retrieve user profile from `TokenManager.getUser()` / ViewModel.
- If user has an avatar or photo, display with `AsyncImage` / Coil.
- If not available, render a clean user profile icon (`Icons.Outlined.Person` or `Icons.Filled.AccountCircle`) in place of Raya AI.

## 5. FAB Position in `StudyPlanScreen`:
- Aligned to `Alignment.BottomStart` with Persian RTL layout direction, placing it on the bottom-left of the screen.
