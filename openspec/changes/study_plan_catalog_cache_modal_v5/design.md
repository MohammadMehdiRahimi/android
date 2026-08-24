# Design: Study Plan Catalog Cache, Chapter Search & Summary Confirmation (v5)

## 1. Architecture & Data Flow

### 1.1. Per-Grade Catalog Caching (`CatalogCacheManager` / `CreateStudyPlanViewModel`)
- Implement a thread-safe in-memory cache singleton or cache repository (`Map<String, List<SubjectVisualItem>>`) keyed by grade and major (e.g. `GRADE_12_EXPERIMENTAL`).
- In `CreateStudyPlanViewModel.loadCatalog(grade: String)`:
  1. Check cache for key `${selectedGrade}_${userMajor}`.
  2. If found, immediately update `_state` with cached subjects, set `isLoadingCatalog = false`, and select the first subject.
  3. If not found, set `isLoadingCatalog = true`, make the API call (`safeApiCall { api.getStudyTaskCatalog() }`), parse and map subjects, store in cache, and update `_state` (`isLoadingCatalog = false`).

### 1.2. Skeleton Loader Display
- In `CreateStudyPlanUiState`, `isLoadingCatalog` starts as `true` (unless pre-warmed from cache synchronously).
- `GradeAndBookSection` renders `LazyRow` with `BookItemSkeleton()` items while `isLoadingCatalog` is true.
- `ChapterBlockSkeleton()` renders until chapters are available.

### 1.3. Simplified Chapter Formatting & Right-Aligned Search
- Chapter numbering helper `formatMinimalChapterName(index: Int, rawName: String)`:
  - Strips words like "فصل" and returns concise format: `${persianIndex}: ${cleanedTitle}` or just `${persianIndex}` if no custom title.
- Chapter selection UI:
  - Adds a search TextField at the top of the chapter picker dropdown/sheet.
  - Right-aligns all text items (`TextAlign.Right` / `Alignment.CenterEnd`).
  - Filters chapters in real-time based on query input (matches Persian numerals and keywords).

### 1.4. Summary Confirmation Modal (`StudyPlanSummaryModal`)
- State management:
  - Add `isSummaryModalVisible: Boolean` in `CreateStudyPlanUiState`.
  - Clicking "ذخیره برنامه مطالعاتی" sets `isSummaryModalVisible = true` without calling backend.
- Modal presentation:
  - Styled with Material 3 `ModalBottomSheet` or `Dialog`.
  - Renders:
    - Target Grade & Field of Study
    - Selected Book minimal name
    - Selected Chapters and number of chosen topics
    - Number of Study Periods
    - Timing details (auto calculated or manual slot chips with times)
  - Action buttons:
    - "ویرایش و بازگشت" (Dismiss modal)
    - "تایید نهایی و ثبت برنامه" (Calls `submitPlanToBackend()`, shows loading indicator on button, dismisses on success and navigates back).

### 1.5. Back Navigation
- The top back icon button in `CreateStudyPlanScreen` is wired to navigate back to the Tasks/Study Plan screen (`study_plan` destination / `popBackStack` or tab selection).

## 2. RTL & Persian Typography
- Strict Right-to-Left alignment for chapter list, search input, and summary cards.
- All numbers converted to Persian numerals via `toPersianNumber()`.
- Persian font family (`IranSansFontFamily`) applied consistently.
