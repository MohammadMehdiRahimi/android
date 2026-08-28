# Proposal: Remove Top Statistics Bar ("رتبه من", "ناشگر برتر", ...) from Home Screen

## 1. Problem Statement & User Intent
The user requested to completely remove the top statistics cards section containing "رتبه من" (My Rank), "ناشگر برتر" (Top Badge / League), "کل مطالعه" (Total Study Time), and "امتیاز من" (My Points) from the main home dashboard screen (`ReferenceHomeDashboard.kt` / `HomeScreenContent`).

Removing this component simplifies the home screen hierarchy, eliminating redundant metric cards and allowing the user to immediately view the visual Performance Chart and the core Feature Grid directly below the profile header.

---

## 2. Scope of Changes
- **Home Screen Presentation (`ReferenceHomeDashboard.kt`)**:
  - Remove `HomeStatsRow` from the `ReferenceHomeDashboard` Composable hierarchy.
  - Remove unused helper composables/stat card dependencies from the home dashboard rendering path.
  - Retain the clean profile top header (`HomeTopHeader`) with avatar, user name, and notifications bell.
  - Retain the Performance Chart Card (`PerformanceChartCard`) and Feature Grid (`HomeFeatureGrid`).
- **Dashboard Components (`DashboardComponents.kt`)**:
  - Deprecate or remove unused stat cards rows if needed while preserving other reusable components.
- **Testing (`HomeScreenTest.kt`)**:
  - Update UI tests to align with the removal of the top stats cards while asserting the presence of the profile header, performance chart, and feature grid.

---

## 3. Acceptance Criteria
1. The home screen (`HomeScreenContent` / `ReferenceHomeDashboard`) no longer renders "رتبه من", "ناشگر برتر", "کل مطالعه", or "امتیاز من" stat cards.
2. The user profile header (`HomeTopHeader`) remains intact and properly aligned with avatar, name, and notification bell.
3. The Performance Chart Card and feature grid remain fully functional and visually aligned.
4. All unit and UI tests (`HomeScreenTest`) compile and pass.
