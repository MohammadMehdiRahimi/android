# Actionable Tasks: Event-Driven Study Task Focus Timer & Execution API Integration

## 1. Network & Data Layer
- [ ] Define Event Types & Outcome Enums/Constants in `network/ApiService.kt` (`StudyExecutionEventType`, `CompletionOutcome`, `ExecutionStatus`).
- [ ] Define `ManualTaskEventRequestDto` with `clientEventId`, `expectedSequence`, `type`, `occurredAt`, `completionOutcome`, `completionPercent`, and `note`.
- [ ] Define `ManualTaskExecutionResponseDto` with `id`, `manualTaskId`, `status`, `eventSequence`, `actualSeconds`, `persistedActiveSeconds`, `timerElapsedSeconds`, `pausedSeconds`, `completionPercent`, `startedAt`, and `finishedAt`.
- [ ] Add `@POST("study-execution/me/manual-tasks/{taskId}/events")` endpoint to `ApiService.kt`.
- [ ] Update `StudyTaskRepository` interface and `StudyTaskRepositoryImpl` with `sendManualTaskEvent(taskId, request)`.

## 2. Domain Layer
- [ ] Implement `EventTimeHelper` or ISO-8601 UTC timestamp utility.
- [ ] Create `SendManualTaskEventUseCase` for handling UUID generation, timestamp formatting, and dispatching events:
  - Scenario 1: `startTask(taskId)` (`ACTIVITY_STARTED`, `expectedSequence = 0`)
  - Scenario 2: `pauseTask(taskId, sequence)` (`ACTIVITY_PAUSED`)
  - Scenario 3: `resumeTask(taskId, sequence)` (`ACTIVITY_RESUMED`)
  - Scenario 4: `completeTaskFull(taskId, sequence)` (`ACTIVITY_COMPLETED`, FULL, 100)
  - Scenario 5: `completeTaskPartial(taskId, sequence, percent, note)` (`ACTIVITY_COMPLETED`, PARTIAL, 1..99)
  - Scenario 6: `markTaskDone(taskId)` (`ACTIVITY_MARKED_DONE`, `expectedSequence = 0`)

## 3. ViewModel & State Management
- [ ] Enhance `FocusTimerUiState` with:
  - `currentSequence: Int` (starts at 0)
  - `lastExecutionStatus: String?`
  - `showCompletionDialog: Boolean`
  - `completionOutcome: CompletionOutcome`
  - `completionPercent: Int`
  - `completionNote: String`
  - `errorMessage: String?`
- [ ] Implement event dispatching methods in `FocusTimerViewModel`:
  - `onStartTimer()` -> dispatches `ACTIVITY_STARTED`, updates `currentSequence` from response.
  - `onPauseTimer()` -> dispatches `ACTIVITY_PAUSED`, updates `currentSequence`.
  - `onResumeTimer()` -> dispatches `ACTIVITY_RESUMED`, updates `currentSequence`.
  - `onCompleteSession(isFull, percent, note)` -> dispatches `ACTIVITY_COMPLETED`.
  - `onDismissError()` / sequence reconciliation handler for 409 Conflict.
- [ ] Connect "تکمیل سریع" (Mark as Done) in `StudyPlanViewModel` to dispatch `ACTIVITY_MARKED_DONE`.

## 4. UI Layer (`FocusTimerScreen.kt` & Dialogs)
- [ ] Ensure all action buttons in `FocusTimerScreen.kt` have test tags:
  - `Modifier.testTag("timer_start_button")`
  - `Modifier.testTag("timer_pause_button")`
  - `Modifier.testTag("timer_resume_button")`
  - `Modifier.testTag("timer_finish_button")`
- [ ] Build completion dialog / bottom sheet in `FocusTimerComponents.kt`:
  - Radio buttons / toggles for "پایان کامل" (۱۰۰٪) vs "پایان ناقص" (نیمه‌کاره).
  - Persian Slider / progress picker for percent when partial.
  - Persian OutlinedTextField for entering study notes.
  - Confirm and Cancel buttons.
- [ ] Ensure Persian digits and RTL alignment across all timer labels and dialog inputs.
- [ ] Add Persian error snackbar / banner for handling network errors and 409 sequence mismatch.

## 5. Testing & Verification
- [ ] Unit Tests:
  - Create `SendManualTaskEventUseCaseTest` to verify payload generation for all 6 scenarios.
  - Create `FocusTimerViewModelTest` to verify sequence incrementation and state transitions.
- [ ] Robolectric / UI Tests:
  - Test timer start, pause, resume, and completion dialog flow in `FocusTimerScreenTest`.
- [ ] Build Verification:
  - Run `compile_applet` to confirm zero compilation errors.
