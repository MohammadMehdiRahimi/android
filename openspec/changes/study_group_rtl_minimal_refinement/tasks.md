# Tasks: Study Groups RTL Refinement, Minimalist Layout & Typography Optimization

- [ ] **1. Model & Data Adjustments (`com.example.ui.features.mygroup.MyGroupModels.kt`)**
  - [ ] 1.1 Update `PersonalGroupStats` and `GroupMemberUiModel` to make task count optional or remove task count dependencies where appropriate.

- [ ] **2. UI Components & RTL Alignment (`com.example.ui.features.mygroup.MyGroupComponents.kt`)**
  - [ ] 2.1 Refactor TopBar to be compact with smaller 17sp title and neat back button.
  - [ ] 2.2 Refactor Search Box & Filter Chips: RTL text alignment, compact padding (44-46dp height), 12sp fonts.
  - [ ] 2.3 Refactor State 1 Empty Card: 160dp illustration, 16sp title, 12sp subtitle, sleek 44dp CTA buttons.
  - [ ] 2.4 Refactor Group Identity Card: 54dp mountain icon, 16sp group name, 12sp motto, smaller badge chips, 15sp stats.
  - [ ] 2.5 Refactor Active Battle Card: Compact layout, 13sp titles, 15sp scores, 6dp comparison progress bar.
  - [ ] 2.6 Refactor Personal Stats Grid: Convert to a 3-item grid (رتبه من, امتیاز من, ساعت مطالعه), remove "تعداد تسک", compact design.
  - [ ] 2.7 Refactor Members Table: Remove "تعداد تسک" column, strictly order columns in RTL (رتبه -> عضو -> ساعت مطالعه -> امتیاز), compact rows with 32dp avatars and 12-13sp typography.
  - [ ] 2.8 Refactor Medals Tab: Compact grid with smaller icons and 12sp badge names.

- [ ] **3. Screen Integration (`com.example.ui.features.mygroup.MyGroupScreen.kt`)**
  - [ ] 3.1 Verify outer RTL wrapper `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl)`.
  - [ ] 3.2 Update dialogs and interactive callbacks to match the minimalist style.

- [ ] **4. Testing & Verification**
  - [ ] 4.1 Update `MyGroupDualStateTest.kt` for the 3-item personal stats and removed task count column.
  - [ ] 4.2 Run unit tests and compile applet to guarantee zero regression.
