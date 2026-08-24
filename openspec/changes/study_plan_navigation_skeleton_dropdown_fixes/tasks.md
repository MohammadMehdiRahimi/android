# Tasks: Study Plan Navigation, Granular Skeleton, and Chapter Dropdown Fixes

## 1. Navigation & Backstack Tasks
- [ ] Update `CreateStudyPlanScreen.kt` back navigation handlers:
  - Fix top-bar back arrow and system `BackHandler` to safely pop the backstack to `dashboard` (retaining `ShetabBottomNavigation`).
  - Eliminate standalone routing to `"study_plan"` when returning from create screen.

## 2. Granular Skeleton & Study Plan Tasks
- [ ] Refactor `StudyPlanSummaryCard` in `StudyPlanComponents.kt` to accept `isLoading: Boolean = false`.
- [ ] Add granular shimmer placeholders specifically for numerical metrics while keeping all static Persian labels and icons fully rendered.
- [ ] Update `StudyPlanFilterRow` and `StudyPlanScreen.kt` so static filter buttons remain visible during data fetch.
- [ ] Refactor `StudyPlanScreen.kt` and `MainScreen.kt` tab 1 loading behavior to show the structured granular skeleton.

## 3. Chapter Dropdown & Search UX Tasks
- [ ] Wrap Chapter selection dropdown in `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl)`.
- [ ] Re-engineer the search input inside chapter selection:
  - Set compact height (38–40dp) with `maxLines = 1` and `softWrap = false` on `"جستجوی فصل..."` to prevent text stacking/wrapping.
  - Implement smooth, stable anchoring that prevents keyboard-induced dropdown jumping during typing.
- [ ] Verify RTL alignment of all chapter titles, minimal numbers, and selection checkmarks.

## 4. Verification & Testing Tasks
- [ ] Update and run unit / Robolectric test suites (`gradle :app:testDebugUnitTest`).
- [ ] Verify build compilation via `compile_applet`.
