# Tasks: Focus Timer Navigation & Backend API Integration

## 1. Study Plan Task Start Navigation
- [ ] In `StudyPlanScreen.kt`:
  - Wire `onStartTask` to `navController.navigate("focus_timer/${task.id}")`.

## 2. Focus Timer Screen Backend Connectivity
- [ ] In `FocusTimerScreen.kt`:
  - Inject or reference `ApiClient.apiService` and `TokenManager`.
  - Connect timer start, pause, resume, and finish actions to call `postItemExecutionEvent` / `postManualTaskExecutionEvent`.
  - Implement task details fetching on screen launch if available.

## 3. Verification & Testing
- [ ] Compile applet via `compile_applet`.
- [ ] Run test suite.
