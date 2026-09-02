# Proposal: Leave Study Group Feature (خروج از گروه)

## 1. Context & Motivation
Users currently in an active study group need a clear, accessible, and safe way to leave their study group (خروج از گروه). 
This feature must:
- Provide an intuitive affordance on the Study Group active member screen (e.g. in the top bar or inside group options / confirmation dialog).
- Display a Persian confirmation dialog with destructive styling (warning about losing group progress/ranking) to prevent accidental exits.
- Invoke the existing `api.leaveGroup()` endpoint via `MyGroupViewModel.leave()`.
- Smoothly transition the user back to State 1 (Non-Member / Search View) upon successful departure with clear Persian feedback.

## 2. Scope & Changes
- **Presentation / UI:**
  - Add a subtle, minimalist "خروج از گروه" button/action in the `MyGroupTopBar` or inside `GroupIdentityCard` (or a 3-dots / more options menu / exit button).
  - Create a Persian `LeaveGroupConfirmationDialog` with:
    - Title: "خروج از گروه"
    - Message: "آیا مطمئن هستید که می‌خواهید از گروه خارج شوید؟ با خروج، امتیازات و رتبه گروهی شما بازنشانی خواهد شد."
    - Confirm button: "خروج" (Destructive red/error styling)
    - Dismiss button: "انصراف"
  - Wire the exit action to `viewModel.leave()`.
- **ViewModel & State Management:**
  - Leverage existing `leave()` function in `MyGroupViewModel` which executes `api.leaveGroup()` and reloads the screen to switch to the non-member state (`isMember = false`).
- **Feedback & Toast:**
  - Show Persian success/error toast messages.
- **Testing:**
  - Add unit/Robolectric test case in `MyGroupDualStateTest.kt` verifying the leave group state transition.

## 3. Acceptance Criteria
- [x] A sleek, accessible "خروج از گروه" action is available for active members in the Study Group screen.
- [x] Clicking the action triggers a confirmation dialog in Persian.
- [x] Confirming the action calls `leaveGroup` API and handles loading state (`busy`).
- [x] Upon success, the UI transitions to State 1 (Non-Member / Search & Create View).
- [x] Automated tests cover the leave functionality.
