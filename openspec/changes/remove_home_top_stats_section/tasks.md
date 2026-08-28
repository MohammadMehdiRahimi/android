# Tasks Checklist: Remove Top Stats Section from Home Screen

- [x] **1. UI Presentation Layer (`ReferenceHomeDashboard.kt`)**
  - [x] Remove `HomeStatsRow` call from `ReferenceHomeDashboard`.
  - [x] Remove unused `HomeStatsRow` and `HomeStatCard` private composable functions.

- [x] **2. UI Testing (`HomeScreenTest.kt`)**
  - [x] Update `referenceHomeDashboard_rendersAllRedesignedComponents` test in `HomeScreenTest.kt`.
  - [x] Run JVM Robolectric tests to verify all assertions pass.

- [x] **3. Verification & Compilation**
  - [x] Run `compile_applet` to ensure full build success.
