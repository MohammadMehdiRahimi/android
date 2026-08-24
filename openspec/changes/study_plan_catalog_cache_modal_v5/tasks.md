# Tasks: Study Plan Catalog Cache, Chapter Search & Summary Confirmation (v5)

## Data & ViewModel Layer
- [ ] Implement caching mechanism (`CatalogCacheManager` or singleton cache map) to cache catalog subjects and chapters per grade/major.
- [ ] In `CreateStudyPlanViewModel`:
  - [ ] Initialize with `isLoadingCatalog = true` when cache miss occurs; fetch and cache response.
  - [ ] Add `showSummaryModal()` and `hideSummaryModal()` actions.
  - [ ] Add `submitPlanToBackend()` triggered exclusively from the summary modal.
  - [ ] Support on-demand search query state for chapter selection if needed.

## Presentation Layer (`CreateStudyPlanScreen.kt`)
- [ ] Update `formatPersianChapterName` -> `formatMinimalChapterName`:
  - [ ] Output "۱", "۲", ... or "۱: [عنوان فصل]" instead of "فصل ۱".
- [ ] Refactor Chapter Selection:
  - [ ] Align chapter item text strictly to the right (`TextAlign.Right` / `TextAlign.Start` with RTL).
  - [ ] Add search field with clean Persian placeholder ("جستجوی فصل...") to filter chapters dynamically.
- [ ] Verify Book Skeleton:
  - [ ] Ensure `GradeAndBookSection` shows `BookItemSkeleton()` when `isLoadingCatalog == true`.
- [ ] Implement `StudyPlanSummaryModal`:
  - [ ] Display comprehensive, beautifully styled summary of the study plan.
  - [ ] Include "تایید نهایی و ثبت" (triggers API save) and "ویرایش و بازگشت" (closes modal).
  - [ ] Integrate into `CreateStudyPlanScreen` scaffold/dialog layer.
- [ ] Connect Back button navigation to return reliably to the Tasks/Study Plan screen (`study_plan`).

## Testing & Verification
- [ ] Update and write unit tests in `CreateStudyPlanTest.kt` verifying:
  - [ ] Per-grade caching behavior (no duplicate network requests).
  - [ ] Skeleton loading state lifecycle.
  - [ ] Summary modal open/close and final submission flow.
  - [ ] Chapter search filtering and minimal formatting ("1", "2").
- [ ] Run `compile_applet` and `gradle :app:testDebugUnitTest`.
