# Tasks: Study Plan and Tasks Refinements (v4)

## Presentation Layer (Tasks Screen)
- [ ] In `StudyPlanComponents.kt`:
  - [ ] Refactor `StudyPlanTopHeader` to remove user avatar box, online indicator, notification bell icon, and the subtitle ("کارهای امروزت رو مدیریت کن").
  - [ ] Render the centered title "برنامه مطالعاتی" with clean vertical spacing.
- [ ] In `StudyPlanScreen.kt`:
  - [ ] Adjust `AddTaskFloatingActionButton` modifier to align tightly above the bottom navigation bar (`bottom = 16.dp`).

## Presentation Layer (Create Study Plan Screen)
- [ ] In `CreateStudyPlanScreen.kt`:
  - [ ] Add granular shimmer/skeleton components for the books row (`BookItemSkeleton`) and chapter selector card (`ChapterBlockSkeleton`).
  - [ ] Show skeleton placeholders when books/chapters are loading, while keeping the full page structure (top bar, grade dropdown, periods, timing, save button) visible.
  - [ ] Verify `CreatePlanTopBar` top alignment with `statusBarsPadding()` and compact padding so "ایجاد برنامه مطالعاتی" sits cleanly at the top without getting occluded.
  - [ ] In `TimingSection`, scale down the manual timing toggle switch and remove any borders.

## Testing & Verification
- [ ] Run `compile_applet` to verify compilation.
- [ ] Update and run unit tests in `CreateStudyPlanTest.kt` and `StudyPlanTest.kt`.
