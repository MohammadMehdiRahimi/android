# Proposal: Update Home Dashboard Card Titles & Performance Chart Positioning

## 1. Problem Statement & User Intent
The user requested three specific design & copywriting refinements on the Home Screen (`ReferenceHomeDashboard.kt`):
1. Update card titles:
   - "برنامه‌ریز هوشمند" / "برنامه‌ریز هوشمند شتاب" -> "برنامه درسی"
   - "لایـک‌های رقابتی فعال" -> "لیگ‌های رقابتی"
2. Move the performance chart higher up on the screen (reducing the top gap between the header and the chart card).
3. Change chart title from "نمای کلی عملکرد" -> "نمای کلی".

---

## 2. Scope of Changes
- **Home Dashboard Presentation (`ReferenceHomeDashboard.kt`)**:
  - Update `FeatureCardSmartPlan` title and subtitle to reflect "برنامه درسی".
  - Update `FeatureCardLeague` title to "لیگ‌های رقابتی".
  - Update `PerformanceChartCard` header title to "نمای کلی".
  - Refine spacing/padding between `HomeTopHeader` and `PerformanceChartCard` (reduce vertical gap to pull chart higher up).
- **Unit & Compose UI Tests (`HomeScreenTest.kt`)**:
  - Update test assertions to match the new Persian text copies ("برنامه درسی", "لیگ‌های رقابتی", "نمای کلی").

---

## 3. Acceptance Criteria
1. The tall card displays "برنامه درسی" instead of "برنامه‌ریز هوشمند شتاب".
2. The league card displays "لیگ‌های رقابتی" instead of "لایـک‌های رقابتی فعال".
3. The chart card title displays "نمای کلی" instead of "نمای کلی عملکرد".
4. Spacing above the chart is reduced so it sits closer to the top header.
5. All Robolectric and UI tests pass and `compile_applet` succeeds.
