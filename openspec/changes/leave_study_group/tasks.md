# Tasks: Leave Study Group Feature (خروج از گروه)

- [ ] **1. String Resources (`res/values/strings.xml`)**
  - [ ] 1.1 Add Persian strings for `group_leave_title`, `group_leave_confirm_message`, `group_leave_action`, `group_leave_cancel`, and `group_leave_success`.

- [ ] **2. UI Components & Dialog (`MyGroupComponents.kt`)**
  - [ ] 2.1 Update `MyGroupTopBar` to support an optional `onLeaveGroupClick` icon button when `isMember == true`.
  - [ ] 2.2 Implement `LeaveGroupConfirmDialog` with RTL layout, destructive red confirm button, and cancel action.

- [ ] **3. Screen & State Integration (`MyGroupScreen.kt`)**
  - [ ] 3.1 Manage `showLeaveDialog` state in `StudyGroupScreen`.
  - [ ] 3.2 Pass `onLeaveGroupClick = { showLeaveDialog = true }` to `MyGroupTopBar`.
  - [ ] 3.3 Render `LeaveGroupConfirmDialog` and connect confirm callback to `viewModel.leave()`.

- [ ] **4. Testing & Verification**
  - [ ] 4.1 Add test case in `MyGroupDualStateTest.kt` verifying `leave()` action and dialog state.
  - [ ] 4.2 Compile applet and verify zero errors.
