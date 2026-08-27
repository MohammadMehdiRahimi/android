# Tasks: Home Dashboard Filters, Granular Shimmers, and Study Plan Sort Text Removal

## 1. Study Plan Tasks Tasks
- [ ] In `StudyPlanComponents.kt`:
  - Remove "مرتب سازی" button and text from `RemainingTasksSectionHeader`.

## 2. Home Screen Performance Chart Tasks
- [ ] In `ReferenceHomeDashboard.kt`:
  - Add metric filter enum / state (`DashboardMetricType` with options: "امتیاز", "ساعت", "تست").
  - Add second dropdown pill beside the timeframe selector in `PerformanceChartCard`.
  - Adapt `PerformanceCanvasChart` Y-axis scaling, peak tooltip unit, and data mapping according to the selected metric.

## 3. Granular Shimmers on Home Dashboard
- [ ] In `ReferenceHomeDashboard.kt`:
  - Update `HomeStatCard` to accept `isLoading: Boolean`.
  - Render a shimmer box for the value in `HomeStatCard` when `isLoading = true`.
  - Apply `isLoading` to "رتبه من", "کل مطالعه", and "امتیاز من" in `HomeStatsRow`.
  - Add smooth skeleton shimmer inside `PerformanceChartCard` chart area when `loading = true`.

## 4. Verification Tasks
- [ ] Compile applet via `compile_applet`.
- [ ] Run unit tests via `gradle :app:testDebugUnitTest`.
