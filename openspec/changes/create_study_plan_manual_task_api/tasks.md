# Actionable Tasks: Refinement of Create Study Plan UI & Manual Task Creation API Integration

## 1. Network & Data Layer
- [x] Add `CreateManualStudyTaskDto` and `ManualStudyTaskResponseDto` to `ApiService.kt`.
- [x] Add `@POST("study-tasks/me/manual")` endpoint definition to `ApiService.kt`.

## 2. ViewModel & State
- [x] Update `CreateStudyPlanUiState` to support:
  - `isManualTiming` (default `false`).
  - `chapterBlocks: List<ChapterBlockState>`.
- [x] Implement `addChapterBlock()`, `removeChapterBlock()`, `selectChapterForBlock()`, and `toggleTopicForBlock()`.
- [x] Implement `saveStudyPlan()`:
  - Validates date, duration, and topics.
  - Generates UUID `requestId`.
  - Dispatches API calls to `api.createManualStudyTask(...)`.
  - Handles 201, 400, 404, 409 error codes with Persian messages.

## 3. UI Layer (`CreateStudyPlanScreen.kt`)
- [x] Remove subtitle "برنامه اختصاصی خود را در چند گام بساز".
- [x] Make book name display minimal and clean.
- [x] Fix Chapter dropdown menu to anchor directly below the selector with matching styling and no green border.
- [x] Render dynamic list of Chapter/Topic blocks; "اضافه کردن فصل" adds a new block below.
- [x] Redesign Timing section:
  - Label toggle "زمان‌بندی دستی" (defaults to unchecked).
  - Hide sliders when unchecked; show when checked.
  - Implement custom slider with thick capsule track, step dots, and vertical purple pill thumb in LTR.

## 4. Verification
- [x] Run `compile_applet` to verify compilation.
- [x] Verify test suite runs cleanly.
