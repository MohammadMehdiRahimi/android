# Proposal: Study Plan and Tasks Refinements (v4)

## 1. Problem Statement & Motivation
Users identified several UI and UX adjustments needed across both the Study Plan / Tasks screen (`StudyPlanScreen`) and the Create Study Plan screen (`CreateStudyPlanScreen`):
1. **Study Tasks Screen (`StudyPlanScreen`):**
   - The subtitle text "کارهای امروزت رو مدیریت کن" in the header clutters the top section.
   - The user profile avatar and notification bell in `StudyPlanTopHeader` take up excessive vertical space and visual attention on a task-focused dashboard.
   - The Add Task floating action button (`+`) is elevated too high (`bottom = 85.dp`) above the bottom navigation bar instead of sitting closely above it.
2. **Create Study Plan Screen (`CreateStudyPlanScreen`):**
   - When fetching catalog/book data, the screen should not block or hide the surrounding structure. It needs targeted skeleton loaders for the books list and chapter selection block while keeping the rest of the page visible.
   - The screen title "ایجاد برنامه مطالعاتی" needs to be positioned cleanly at the top safe area without colliding with the device status bar.
   - The manual timing toggle switch needs a more compact height and must be borderless.

## 2. Scope & Acceptance Criteria
*   **Tasks Screen Header Cleanup:** Remove subtitle "کارهای امروزت رو مدیریت کن", remove user avatar box and notification bell icon from `StudyPlanTopHeader`, presenting a clean, centered title.
*   **Tasks Screen FAB Positioning:** Anchor the `+` FAB directly above the bottom navigation bar (`bottom = 16.dp`).
*   **Create Plan Granular Skeleton Loader:** When book information is being retrieved, render shimmer/skeleton placeholders specifically for the horizontal books row and chapter selector, while rendering the surrounding page components (top bar, grade selector, study periods, timing section, save button).
*   **Create Plan Safe Area Top Alignment:** Position "ایجاد برنامه مطالعاتی" safely at the top using `statusBarsPadding()` with optimal compact spacing.
*   **Create Plan Compact Borderless Switch:** Reduce the height and footprint of the manual timing toggle, removing any border stroke.
