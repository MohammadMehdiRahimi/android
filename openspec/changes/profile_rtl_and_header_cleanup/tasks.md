# Tasks Checklist: Profile RTL Alignment & Header Removal

- [x] **1. Presentation Layer (`ProfileScreen.kt`)**
  - [x] Enforce `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl)`.
  - [x] Remove the top header (notification bell & mini online avatar).
  - [x] Reorder Hero Card elements so Avatar is on the Right (Start in RTL) and User Info (Name, Grade, Field) is on the Left (End in RTL), matching `profile.png`.
  - [x] Ensure Personal Info Rows have title on Right (Start) and Value + Arrow on Left (End).
  - [x] Ensure Account Action Cards have Icon + Texts on Right (Start) and Arrow on Left (End).
  - [x] Align section headers with Icon on Right and Title on Left.

- [x] **2. Verification & Testing**
  - [x] Run `compile_applet` to confirm successful build.
  - [x] Verify test suite in `HomeScreenTest.kt`.
