# Technical Design: Remove Top Stats Section from Home Screen

## 1. Architectural Overview
This UI refinement modifies the Presentation Layer of the Home Dashboard (`ui/main/ReferenceHomeDashboard.kt`), streamlining the visual structure by removing `HomeStatsRow`.

### Visual Hierarchy (Before):
```
[ HomeTopHeader: Avatar + User Name + Subtitle + Notification Bell ]
[ HomeStatsRow: "رتبه من" | "ناشگر برتر" | "کل مطالعه" | "امتیاز من" ]  <-- REMOVE
[ PerformanceChartCard: Performance graph + Range selector ]
[ HomeFeatureGrid: Smart Study Plan, Groups, Exams, Leitner... ]
```

### Visual Hierarchy (After):
```
[ HomeTopHeader: Avatar + User Name + Subtitle + Notification Bell ]
[ PerformanceChartCard: Performance graph + Range selector ]
[ HomeFeatureGrid: Smart Study Plan, Groups, Exams, Leitner... ]
```

---

## 2. Component Modifications

### 2.1 `ReferenceHomeDashboard.kt`
- Remove invocation of `HomeStatsRow(dashboard = dashboard, loading = loading)` inside `ReferenceHomeDashboard`.
- Clean up unused composable functions (`HomeStatsRow`, `HomeStatCard`) to avoid dead code in the file.
- Ensure `PerformanceChartCard` seamlessly follows `HomeTopHeader` with proper 16.dp vertical spacing.

### 2.2 `HomeScreenTest.kt`
- Update assertion assertions:
  - Remove assertions expecting "رتبه من", "کل مطالعه", "امتیاز من", and the duplicate "ناشگر برتر" in the stats row.
  - Retain assertions for user profile title, "نمای کلی عملکرد", chart intervals, and the feature grid cards.

---

## 3. RTL & Performance Impact
- Zero negative impact on RTL layout.
- Marginally reduces initial composition cost and eliminates unnecessary stat card recompositions during dashboard fetch.
