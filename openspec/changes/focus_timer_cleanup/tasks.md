# Tasks: Focus Timer Cleanup

## Presentation Layer (UI)
- [ ] In `app/src/main/java/com/example/ui/features/studyplan/FocusTimerScreen.kt`:
  - [ ] Remove `TimerStyle` enum definition.
  - [ ] Remove `showCompletionSheet`, `showSettings`, and `currentStyle` state variables.
  - [ ] Delete the `if (currentStyle == TimerStyle.LUXURY_FULLSCREEN)` block.
  - [ ] Un-nest the `else` block containing the circular timer and make it the main UI.
  - [ ] Remove the Settings button from the top app bar in the circular timer UI.
  - [ ] Remove the Settings button from the downward arc action controls in the circular timer UI.
  - [ ] Remove the green "Finish Task" (اتمام تسک) button from the action controls.
  - [ ] Delete the `showSettings` ModalBottomSheet block.
  - [ ] Delete the `showCompletionSheet` TaskCompletionBottomSheet block.
  - [ ] Remove `TimerStyleSelector` and `TimerSettingItem` usage (and their definitions from `FocusTimerComponents.kt` if no longer used).
