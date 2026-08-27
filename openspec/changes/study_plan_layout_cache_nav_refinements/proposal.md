# Proposal: Study Plan Layout Stability, In-Memory Caching, and Navigation Refinements

## Problem Statement
1. **Layout Shift during Data Loading in Study Plan (`StudyPlanScreen` / `StudyPlanSkeletonLoading` / `MainScreen` Tab 1):**
   When the user switches to the Study Plan tab (وظایف برنامه), an artificial delay in `MainScreen` along with double header/padding rendering caused the UI elements to jump upwards and mismatch the structure of the actual loaded screen.
2. **Excessive / Unnecessary Data Refetching:**
   The user explicitly requested that the study plan tasks data must ONLY be re-fetched from the server when a task is created (اضافه), deleted (حذف), or updated/edited (ادیت یا تغییر وضعیت). In all other cases (e.g. switching between tabs, returning from other screens, re-rendering), the cached data in memory / repository must be used immediately without refetching or showing loading spinners.
3. **Seamless Navigation after Task Creation:**
   After saving a study plan / tasks in `CreateStudyPlanScreen`, the user must be cleanly transitioned back to the Study Plan tasks page (`study_plan` / tab 1 in `MainScreen`), and the newly created tasks must immediately appear via cache invalidation/refresh.

## Proposed Changes
1. **Layout Stability in Study Plan Screen & Skeleton:**
   - Align padding and structure between skeleton loading and loaded content so that the header, summary card, filter row, and task cards occupy identical vertical coordinates.
   - Remove artificial `delay(350)` in `MainScreen` tab transition so that cached data renders instantly without flashing skeletons.
2. **Strict In-Memory Caching in `StudyPlanViewModel` & Cache Invalidation:**
   - Maintain an in-memory repository cache for study tasks by date.
   - Only trigger API calls to `getStudyTasks` if the cache for the selected date is empty OR when explicitly triggered by a mutating event:
     - Task creation (اضافه کردن تسک دستی یا ثبت برنامه مطالعه در `CreateStudyPlanViewModel`)
     - Task cancellation/deletion (حذف تسک در `cancelTask`)
     - Task status modification/execution event (تغییر وضعیت یا ادیت در `submitSingleEvent` / `markTaskDone` / `startTask`)
   - When switching tabs or revisiting the study plan screen, reuse the existing loaded `day` and `catalog` from cache without performing background network requests or resetting state to loading.
3. **Accurate Navigation to Study Plan Tasks:**
   - After saving in `CreateStudyPlanScreen` (`confirmAndSubmitPlan` / `PlanSaved`), ensure navigation returns directly to the Study Plan screen (Tab index 1 in `MainScreen` / `dashboard` with `initialTab = 1`), and flag the cache for refresh so the new tasks are loaded and displayed.

## Acceptance Criteria
- [x] The UI of the study plan screen does not jump or shift upwards when loading or switching tabs.
- [x] Tasks data is only re-fetched upon add, edit, or delete operations. In all other scenarios, cached data is used instantly.
- [x] After saving a study plan in `CreateStudyPlanScreen`, navigation smoothly lands on the study plan tasks view with the newly saved tasks visible.
