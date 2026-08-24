# Actionable Tasks: Create Study Plan Page Refinements

## 1. ViewModel Modifications
- [ ] Update `selectChapterForBlock` in `CreateStudyPlanViewModel.kt` to reset `selectedTopicIds` to `emptySet()` upon selecting a chapter.

## 2. Screen UI Modifications
- [ ] Reduce top spacing/padding for the header row in `CreateStudyPlanScreen.kt`.
- [ ] Convert book items into text-only selectable boxes/chips without cover images.
- [ ] Replace inline expanding container with a floating overlay popup anchored to the selector box with Persian chapter numbers (e.g. فصل ۱, فصل ۲, ...).
- [ ] Make the timing toggle trigger button more compact with smaller height and padding.

## 3. Testing & Verification
- [ ] Update unit tests in `CreateStudyPlanTest.kt` to verify that selecting a chapter initializes with empty topics.
- [ ] Run `compile_applet` to ensure error-free build.
