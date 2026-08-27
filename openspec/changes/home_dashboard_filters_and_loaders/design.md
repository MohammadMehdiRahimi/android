# Design: Home Dashboard Filters, Granular Shimmers, and Study Plan Sort Text Removal

## Architectural & UI Design

### 1. Study Plan Tasks Sort Removal
- In `StudyPlanComponents.kt`:
  - Modify `RemainingTasksSectionHeader`:
    - Remove the left-side `Row` containing `Icons.Default.KeyboardArrowDown` and `stringResource(R.string.study_plan_sort_button)`.
    - Keep the title ("تسک‌های باقی‌مانده") and badge count cleanly aligned.

### 2. Home Performance Chart Metric Filter
- In `ReferenceHomeDashboard.kt`:
  - Define enum / state `DashboardMetricType` with values:
    - `HOURS("ساعت", "ساعت")`
    - `POINTS("امتیاز", "امتیاز")`
    - `TESTS("تست", "تست")`
  - In `PerformanceChartCard`:
    - Display a `Row` containing two dropdown pills on the left side (RTL end):
      1. Timeframe filter (`rangeTitles`: "هفته گذشته", "ماه گذشته", etc.)
      2. Metric filter (`metricTitles`: "ساعت", "امتیاز", "تست")
    - Both pills follow M3 design with rounded corners (`12.dp`), `#F1F3F9` background, `HomeNavy` text, and `KeyboardArrowDown` icon.
    - Metric calculation:
      - If `HOURS`: uses base hours from buckets.
      - If `POINTS`: scales bucket values to point metrics.
      - If `TESTS`: scales bucket values to test question counts.
      - Tooltip and Y-axis units update accordingly with Persian numerals.

### 3. Granular Shimmers on Home Screen
- In `ReferenceHomeDashboard.kt`:
  - `HomeStatCard`:
    - Add `isLoading: Boolean` parameter and optional `shimmerWidth: Dp`.
    - When `isLoading == true`:
      - The `value` text is replaced by a `Box(modifier = Modifier.width(36.dp).height(14.dp).shimmerEffect(RoundedCornerShape(4.dp)))`.
      - Title, icon, background card, and accent bar remain fully rendered and static.
  - In `HomeStatsRow`:
    - Pass `isLoading = loading && dashboard == null` for:
      - "رتبه من"
      - "کل مطالعه"
      - "امتیاز من"
  - In `PerformanceCanvasChart`:
    - When `loading == true`:
      - Overlay or render a subtle shimmer wave skeleton over the chart area (`height(200.dp)`), keeping the outer card, title, and filter buttons static.

## RTL & Persian Localization
- Strict RTL layout with Persian number formatting (`toPersianNumber()`) and IranSans typography.
- Layout direction: `LocalLayoutDirection provides LayoutDirection.Rtl`.
