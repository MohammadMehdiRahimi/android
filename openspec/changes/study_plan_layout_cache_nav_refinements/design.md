# Design: Study Plan Layout Stability, Cache Management, and Post-Save Navigation

## Architecture & Data Flow

### 1. In-Memory Cache Store (`StudyPlanDataCache`)
To satisfy the strict condition that tasks are only re-fetched upon adding, editing, or deleting tasks:
- Introduce a singleton `StudyPlanDataCache` object containing:
  - `tasksByDate: MutableMap<String, DailyStudyTasksBodyDto>`: Stores loaded daily tasks mapped by `LocalDate.toString()`.
  - `catalogCache: StudyTaskCatalogBodyDto?`: Stores the loaded catalog.
  - `needsRefresh: AtomicBoolean / Boolean`: Flag set to `true` when a task is created in `CreateStudyPlanViewModel`, removed in `cancelTask`, or modified in `submitEvent`.
- When `StudyPlanViewModel` initializes or selects a date:
  - If `tasksByDate[date]` exists AND `!needsRefresh`: Load immediately from cache into `_state` with `loading = false`. Do NOT initiate network call.
  - If `tasksByDate[date] == null` OR `needsRefresh == true`: Initiate `getStudyTasks`, update `tasksByDate[date]`, and reset `needsRefresh = false`.

### 2. Layout Structure & Metric Alignment (Preventing UI Shifts)
- In `StudyPlanScreen.kt`, unify the layout rendering:
  - Use a single `LazyColumn` for both loading (skeleton items) and loaded states, or ensure `Column` skeleton uses the exact same `statusBarsPadding()` and item vertical spacing (`14.dp`) without redundant headers or offset differences.
  - In `MainScreen.kt`, remove the artificial `delay(350)` in tab crossfade so that cached composables appear instantly without intermediate placeholder flashing.

### 3. Post-Save Navigation Flow
- In `CreateStudyPlanScreen.kt`:
  - When `CreateStudyPlanEvent.PlanSaved` is received, set `StudyPlanDataCache.invalidate()` to ensure the newly saved tasks are fetched fresh.
  - Navigate to `"study_plan"` (or pop to dashboard with tab 1 selected) so the user immediately lands on their study plan tasks screen with up-to-date items.

## RTL & Persian Compliance
- All notifications, error dialogs, and labels strictly follow Iranian Persian wording and RTL formatting.
- Smooth transitions without layout jumping.
