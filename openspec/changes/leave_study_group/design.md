# Design: Leave Study Group Feature (خروج از گروه)

## 1. Architectural Strategy
Adhering to Feature-First Clean Architecture and RTL standards:
- **Presentation Layer**:
  - `MyGroupTopBar`: Add an optional `onLeaveGroupClick: (() -> Unit)?` parameter or an exit icon button with `Icons.AutoMirrored.Filled.Logout` or `Icons.Default.ExitToApp` in the top bar when the user is an active member (`isMember == true`).
  - `LeaveGroupConfirmDialog`: A Material 3 AlertDialog rendered with full RTL direction, containing warning text, red destructive button for exit, and outline button for cancellation.
- **State Flow & ViewModel**:
  - `MyGroupViewModel.leave()` is called upon dialog confirmation.
  - While leaving, `uiState.busy` is set to `true`.
  - On success, `uiState` updates `isMember = false`, `groupDetails = null`, and triggers a snackbar/toast message.
  - If an error occurs, `uiState.errorMessage` is displayed and `busy` reset to `false`.

## 2. Component Design & RTL UX

### 2.1 TopBar Action (Active Member)
- Positioned on the opposite side of the back button.
- Surface icon button with 38dp circle shape, subtle red/rose hover tint (`#FEF2F2`), and `Icons.AutoMirrored.Filled.Logout` in `#EF4444`.
- Accessible `testTag("group_leave_button")`.

### 2.2 Confirmation Dialog
- Title: "خروج از گروه" (16sp Bold, `#DC2626`)
- Content: "آیا از خروج از این گروه مطالعه اطمینان دارید؟ با خروج شما، دسترسی به چالش‌ها و جدول رتبه‌بندی این گروه قطع خواهد شد." (13sp Regular, `#475569`)
- Confirm Button: Red container (`#DC2626`), White text, textTag `leave_group_confirm_button`.
- Cancel Button: Outlined / Text button, Gray text, textTag `leave_group_cancel_button`.

## 3. String Resources
Add corresponding strings to `res/values/strings.xml`:
- `group_leave_title`: "خروج از گروه"
- `group_leave_confirm_message`: "آیا از خروج از این گروه مطالعه اطمینان دارید؟"
- `group_leave_action`: "خروج"
- `group_leave_success`: "شما با موفقیت از گروه خارج شدید"
