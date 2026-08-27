# Design: Focus Timer Navigation & Backend API Integration

## Architectural & Data Flow

### 1. Navigation Flow
- User clicks "شروع" on a `StudyPlanTaskUiModel` in `StudyPlanScreen` / `StudyPlanComponents`.
- Callback `onStartTask(task)` is passed up to `StudyPlanScreen` -> `navController.navigate("focus_timer/${task.id}")`.

### 2. Backend Execution Service Integration
- `FocusTimerScreen`:
  - On launch or timer start:
    - Calls `ApiService.postItemExecutionEvent(itemId = taskId, body = ExecutionEventRequestDto(eventType = "ACTIVITY_STARTED"))` or manual task endpoint if applicable.
  - On pause:
    - Calls `ApiService.postItemExecutionEvent(itemId = taskId, body = ExecutionEventRequestDto(eventType = "ACTIVITY_PAUSED"))`.
  - On resume:
    - Calls `ApiService.postItemExecutionEvent(itemId = taskId, body = ExecutionEventRequestDto(eventType = "ACTIVITY_RESUMED"))`.
  - On completion / save:
    - Calls `ApiService.postItemExecutionEvent(itemId = taskId, body = ExecutionEventRequestDto(eventType = "ACTIVITY_COMPLETED", completionOutcome = "FULL", completionPercentage = 100))`
    - Calls local Room Database to update task status to completed and log study time.
- All network calls are wrapped in `safeApiCall` with coroutine IO dispatchers for resilient offline and network-error resilience.

## RTL & UI Aesthetics
- Persian typography and localized numbers (`toPersianNumber()`).
- High-contrast M3 colors, animated timer rings, full-screen immersive mode, and ambient audio synthesized sound effects.
