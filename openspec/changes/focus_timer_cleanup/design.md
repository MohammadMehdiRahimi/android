# Design: Focus Timer Cleanup

## 1. UI/UX Changes
- **Remove Completion Action:** Remove the green "Finish Task" button from `FocusTimerScreen.kt`.
- **Remove Modals:** Remove `TaskCompletionBottomSheet` instantiation and its state variable `showCompletionSheet`.
- **Remove Settings Access:** Remove the Settings button from the top app bar and the crescent arc controls.
- **Remove Settings Modal:** Remove the `showSettings` bottom sheet logic and its state variable.
- **Unify Timer UI:** Remove the `if (currentStyle == TimerStyle.LUXURY_FULLSCREEN)` branching logic. Promote the `else` block (Circular Modern Timer) to be the root UI for the timer screen.

## 2. State & Logic Cleanup
- Remove the following state variables:
  - `showCompletionSheet`
  - `showSettings`
  - `currentStyle`
- Remove the `TimerStyle` enum completely as it's no longer needed.

## 3. Architecture & Data Flow
- This is a purely presentation-layer change.
- No changes to data models, repositories, or use cases.
- The timer logic (`remainingSeconds`, `isRunning`, `currentMode`) remains intact, continuing to auto-transition Pomodoro phases and sync `StudyExecutionEventDto` to the backend.
