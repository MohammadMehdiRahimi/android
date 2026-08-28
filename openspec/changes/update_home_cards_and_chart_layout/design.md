# Technical Design: Update Home Dashboard Card Titles & Chart Layout

## 1. Architectural Overview
This is a focused Presentation Layer enhancement in `ReferenceHomeDashboard.kt` adhering strictly to RTL typography, Material 3 spacing standards, and Clean Architecture.

---

## 2. Text & Layout Adjustments

### 2.1 Card Titles Refactoring
- **Smart Plan Card (`FeatureCardSmartPlan`)**:
  - `Text(text = "برنامه درسی")`
  - Subtitle: `Text(text = "برنامه‌ریزی دقیق\nو پیگیری دروس")`
  - Content description updated accordingly.
- **League Card (`FeatureCardLeague`)**:
  - `Text(text = "لیگ‌های\nرقابتی")` (or `Text(text = "لیگ‌های رقابتی")` with proper line-breaking for two-line layout).
  - Subtitle retained with polished layout.
- **Chart Card (`PerformanceChartCard`)**:
  - Replace title from "نمای کلی عملکرد" with "نمای کلی".

### 2.2 Vertical Rhythm & Spacing Optimization
- Currently, `Column` in `ReferenceHomeDashboard` uses `Arrangement.spacedBy(16.dp)` and `HomeTopHeader` has `padding(top = 8.dp, bottom = 4.dp)`.
- Reduce the spacing between `HomeTopHeader` and `PerformanceChartCard` to `8.dp` or `10.dp` so the chart moves noticeably higher up.

---

## 3. UI Test Alignments
- Update `HomeScreenTest.kt` with the new strings:
  - Assert `onNodeWithText("نمای کلی")`
  - Assert `onNodeWithText("برنامه درسی")`
  - Assert `onNodeWithText("لیگ‌های\nرقابتی")` or `onNodeWithText("لیگ‌های رقابتی")`
