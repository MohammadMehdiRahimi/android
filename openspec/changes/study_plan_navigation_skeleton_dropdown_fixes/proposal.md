# Proposal: Study Plan Navigation, Granular Skeleton, and Chapter Dropdown Fixes

## 1. Overview & Context
This specification addresses three user experience and visual layout issues in the Study Plan and Create Study Plan features of the Shetab Android application:
1. **Bottom Navigation Loss on Back Navigation:** When navigating back from the Create Study Plan screen (`create_study_plan`) to the Task List screen (`study_plan`), the bottom navigation bar disappears due to popping stack mismatches or directly routing to the standalone screen without the root `ShetabApp` bottom bar context.
2. **Granular Study Plan Screen Skeleton Loading:** When entering the Study Plan / Tasks screen, static structural frames, card outlines, and fixed Persian labels (such as "زمان مطالعه", "باقی‌مانده", "انجام شده", "کل تسک", "همه", "در حال انجام", etc.) must remain visible immediately. Only dynamic backend values and numbers should show shimmering placeholder loaders.
3. **Chapter Dropdown RTL, Search Box Dimensions & Keyboard Stability:** 
   - Ensure the chapter selection items and text are strictly RTL.
   - Fix search field height and prevent placeholder text wrapping ("جستجوی فصل...").
   - Eliminate dropdown upward jumping when the software keyboard opens during typing.

---

## 2. Acceptance Criteria
- [ ] **AC 1 (Bottom Navigation Restoration):** Navigating back from `CreateStudyPlanScreen` (via top back arrow or system BackHandler) reliably returns to the Main Dashboard with the Bottom Navigation Bar visible and the "برنامه‌ریزی" tab active.
- [ ] **AC 2 (Granular Skeleton Loading):** In `StudyPlanScreen` and `MainScreen`, while data is loading from the backend or tab transitions occur, static UI components (Summary card header/labels, filter pills, icons) are rendered with real typography, and only dynamic numbers/values and task cards display clean shimmer indicators.
- [ ] **AC 3 (Strict RTL Chapter Selector):** All chapter dropdown contents, titles, and icons are wrapped in `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl)` and aligned to the start/right.
- [ ] **AC 4 (Compact Search Input & No Text Wrapping):** The chapter search box uses a compact height (38–40dp), `singleLine = true`, and a non-wrapping Persian placeholder.
- [ ] **AC 5 (Stable Layout on Typing):** The chapter selection UI does not jump or reposition abruptly when the keyboard appears.
- [ ] **AC 6 (Automated Verification):** All unit and Robolectric tests pass without failure.
