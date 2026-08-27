# Tasks: Study Plan Layout Stability, Cache Management, and Post-Save Navigation

## 1. Cache & ViewModel Refactoring
- [ ] Create `StudyPlanDataCache` singleton in `StudyPlanViewModel.kt` to hold `tasksByDate`, `catalogCache`, and `needsRefresh` flag.
- [ ] Update `StudyPlanViewModel`:
  - Check cache first on `loadInitial` and `loadDay`. Skip network call if cached data exists and refresh is not needed.
  - Set `needsRefresh = true` and update cache on `createManualTask`, `cancelTask`, and task completion/execution events (`submitSingleEvent` / `markTaskDone`).
- [ ] Update `CreateStudyPlanViewModel`:
  - Mark `StudyPlanDataCache.invalidate()` when a study plan is saved successfully (`confirmAndSubmitPlan`).

## 2. Layout Stability & Shift Prevention
- [ ] Clean up `MainScreen.kt` tab loading logic: remove artificial 350ms skeleton delay when navigating to tab 1.
- [ ] Unify layout padding and spacing in `StudyPlanScreen.kt` and `StudyPlanSkeletonLoading` so the top header, summary matrix, and filter rows have identical pixel coordinates during loading and loaded states.

## 3. Navigation After Task Creation
- [ ] Ensure `CreateStudyPlanScreen.kt` routes cleanly to the Study Plan tasks tab (`initialTab = 1` or popping to dashboard tab 1) upon successful plan creation.

## 4. Verification & Testing
- [ ] Run unit and Robolectric tests via `gradle :app:testDebugUnitTest`.
- [ ] Compile and verify the applet build with `compile_applet`.
