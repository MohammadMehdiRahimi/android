# Proposal: Focus Timer Navigation and Backend Execution Integration

## Problem Statement
When a user taps the "شروع" (Start) button on any study task card (in the study plan screen or task lists):
1. The app should transition to `FocusTimerScreen` (`focus_timer/{taskId}`).
2. It should connect with the backend `StudyExecution` API to record task lifecycle events:
   - `ACTIVITY_STARTED` when the session begins or timer starts.
   - `ACTIVITY_PAUSED` / `ACTIVITY_RESUMED` when the timer pauses or resumes.
   - `ACTIVITY_COMPLETED` when the focus timer finishes or the user saves/completes the session.
3. The newly earned points, completed study seconds, and rank should seamlessly synchronize back to the home dashboard and study plan overview.

## Proposed Changes
1. **Connect Task Card Start Action to FocusTimerScreen:**
   - In `StudyPlanComponents.kt` and `StudyPlanScreen.kt`, ensure `onStartTask(task)` triggers navigation to `focus_timer/${task.id}`.
2. **Integrate Real Backend Events in FocusTimerScreen:**
   - In `FocusTimerScreen.kt`, hook the timer start/pause/complete actions to call `ApiClient.apiService.postItemExecutionEvent` / `postManualTaskExecutionEvent`.
   - Update local state and trigger refresh flows so that study plan and home stats reflect new study time and points.

## Acceptance Criteria
- [x] Tapping "شروع" on any task opens `FocusTimerScreen` with the correct `taskId`.
- [x] Starting the timer sends `ACTIVITY_STARTED` event to backend.
- [x] Completing the session sends `ACTIVITY_COMPLETED` event with outcome and duration to backend.
- [x] User can pause, resume, change sounds, and complete sessions with full offline/graceful fallback.
