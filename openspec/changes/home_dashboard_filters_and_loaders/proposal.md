# Proposal: Home Dashboard Metric Filter, Granular Loading Shimmers, and Study Plan Sort Text Removal

## Problem Statement
1. **Study Plan Tasks Sort Text Removal:**
   In the study plan screen (`StudyPlanComponents.kt`), the header contains a "مرتب سازی" text / button that needs to be removed.
2. **New Metric Filter for Chart on Home Dashboard:**
   Beside the existing time range dropdown ("هفته گذشته"), users need an additional filter selector with options:
   - "امتیاز" (Points / Score)
   - "ساعت" (Hours / Study Time)
   - "تست" (Tests)
3. **Granular Loaders / Shimmers on Home Dashboard:**
   All general sections of the Home screen (Header, Stories, Banners, Feature Grid, League Level, Cards structure) must remain permanently static and rendered immediately without whole-page loading. Loading state and shimmer effects must strictly and exclusively apply to:
   - "مقدار رتبه من" (My Rank stat value)
   - "کل مطالعه" (Total Study stat value)
   - "امتیاز من" (My Points stat value)
   - "نمودار" (Performance Chart area)

## Proposed Changes
1. **Remove Sort Text from Study Plan:**
   - In `RemainingTasksSectionHeader` within `StudyPlanComponents.kt`, remove the "مرتب سازی" button/text.
2. **Add Metric Dropdown Selector in Home Chart Card:**
   - In `PerformanceChartCard` within `ReferenceHomeDashboard.kt`, add a second pill dropdown beside the time range selector for metric types ("امتیاز", "ساعت", "تست").
   - Update chart rendering / Y-axis labels / tooltip according to the chosen metric.
3. **Granular Shimmer Loaders on Home Screen:**
   - Update `HomeStatCard` to support `isLoading: Boolean` and render a rounded shimmer bar instead of placeholder text for "رتبه من", "کل مطالعه", and "امتیاز من".
   - Update `PerformanceChartCard` so that only the chart canvas area displays a shimmer skeleton during loading while preserving the card container, title, and filter pills statically in place.

## Acceptance Criteria
- [x] "مرتب سازی" text is removed from the study plan tasks section header.
- [x] A second filter dropdown with options "امتیاز", "ساعت", and "تست" is placed beside "هفته گذشته" in the home performance card.
- [x] Home dashboard displays all layout structures statically; loading shimmers only appear on My Rank, Total Study, My Points, and the Chart area.
