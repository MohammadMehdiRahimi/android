# Design: Study Plan Navigation, Granular Skeleton, and Chapter Dropdown Fixes

## 1. Architectural Strategy & Scope

### 1.1 Bottom Navigation Retention & Clean Backstack
- In `CreateStudyPlanScreen.kt`:
  - When back action is triggered (top bar or `BackHandler`), invoke `navController.popBackStack()`.
  - If popping the immediate backstack returns `false` (e.g. direct deep link or cleared stack), navigate to `"dashboard"` with `launchSingleTop = true` and ensure tab 1 is selected.
  - Remove all direct navigation to `"study_plan"` as a standalone route from inside the main user flow so that `ShetabBottomNavigation` hosted by `ShetabApp` remains persistent.

### 1.2 Granular Skeleton Architecture for StudyPlanScreen
- Instead of hiding the entire screen with a massive blank shimmer box (`StudyPlanSkeletonLoading` replacing all elements), update `StudyPlanSummaryCard` and `StudyPlanFilterRow`:
  - Support `isLoading: Boolean` parameter in `StudyPlanSummaryCard`.
  - When `isLoading == true`:
    - Display all static labels ("زمان مطالعه", "باقی‌مانده", "انجام شده", "کل تسک") and their respective icons.
    - Replace the numerical values with small shimmer chips (e.g. `width(36.dp)`, `height(16.dp)` with `RoundedCornerShape(4.dp)`).
    - In the bottom progress area, display the static text `"از ... تسک انجام شده"` with a small shimmer block for the count and a shimmer bar for the progress track.
  - In `StudyPlanFilterRow`:
    - Always render the active interactive filter buttons ("همه", "در حال انجام", "انجام شده", "باقی‌مانده") with real text and icons.
  - In `StudyPlanScreen.kt`:
    - When `state.loading && state.day == null`, render the full `LazyColumn` containing the top header, the skeleton-value summary card, the real filter pills, and 3 shimmer task card placeholders.
  - In `MainScreen.kt`:
    - For tab index 1, pass through directly to `StudyPlanScreen` without wrapping in an opaque blocker, allowing smooth granular loading.

### 1.3 Chapter Selection RTL, Search Bar Sizing & Keyboard Resiliency
- In `CreateStudyPlanScreen.kt`:
  - Wrap the chapter menu/selection dropdown in `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl)`.
  - Replace the heavy `OutlinedTextField` with a sleek, compact custom search box (height 38dp, single line, no line break on `"جستجوی فصل..."`, `maxLines = 1`, `softWrap = false`).
  - To prevent keyboard jumping caused by `DropdownMenu` popup repositioning on IME resize, use an inline animated expandable container (or an anchored dropdown with bounded height and `imePadding` / `windowInsets` accommodation). An inline expandable card drawer or a smooth in-card dropdown menu stays anchored in the scrollable hierarchy, ensuring smooth typing without abrupt popup displacement.

---

## 2. Component Design & RTL Layout
- All text alignments use `TextAlign.Right` / `TextAlign.Start`.
- Touch target sizes maintained >= 48dp.
- Font family explicitly set to `IranSansFontFamily`.
- Semantic `testTag` IDs preserved for testing.
