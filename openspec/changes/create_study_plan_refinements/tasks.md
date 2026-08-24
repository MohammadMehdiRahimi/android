# Actionable Tasks: Refinement of Create Study Plan Screen & Catalog API Integration

## 1. Network & Data Layer
- [ ] Ensure `StudyTaskCatalogResponseDto` and `StudyTaskCatalogBookDto` accurately match the response structure with `order` and `topics`.
- [ ] Connect `CreateStudyPlanViewModel` strictly to `api.getStudyTaskCatalog()` without hardcoded or mock fallback dependencies.
- [ ] Support profile completion / 409 Conflict check for `ACADEMIC_PROFILE_INCOMPLETE`.

## 2. ViewModel & State Management
- [ ] Update `CreateStudyPlanViewModel` to parse catalog books directly from `api.getStudyTaskCatalog()`.
- [ ] Update discrete steps for study duration to `[15, 30, 60, 90]` and break duration to `[5, 10, 15, 30]`.
- [ ] Provide functions for selecting chapters, adding chapters, and toggling topics.

## 3. Presentation Layer (`CreateStudyPlanScreen.kt`)
- [ ] Update `CreatePlanTopBar`: remove left forward button and ensure right back button pops back stack to the Task List.
- [ ] Update `TopicChipItem`: decrease size, reduce padding, and adjust font size to 11sp.
- [ ] Update `ChapterAndTopicsSection`: replace "X مورد انتخاب شده" with full-width button "اضافه کردن فصل".
- [ ] Rebuild `TimingSection` to match the exact mockup image:
  - Header with title "زمان‌بندی" on the right and "استفاده از زمان‌بندی پیش‌فرض" toggle on the left.
  - Dashed vertical divider in the middle.
  - Study duration slider (LTR) with `۱۵`, `۳۰`, `۶۰`, `۹۰`.
  - Break duration slider (LTR) with `۵`, `۱۰`, `۱۵`, `۳۰`.
  - Custom purple thumb and track styling.

## 4. Testing & Verification
- [ ] Run `compile_applet` to ensure successful compilation.
- [ ] Verify Robolectric tests pass.
