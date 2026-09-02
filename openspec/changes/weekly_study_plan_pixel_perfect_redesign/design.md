# Design: Refined Weekly Study Plan Components

## 1. Week Days Selector (`PixelPerfectWeekSelector`)
- Layout:
  - Container with border radius `16.dp` and border `1.dp` (`PlanCardBorderColor`).
  - Right arrow button on the right (RTL: start).
  - Horizontally scrollable row containing the days with visible width showing 4 items at a time (`LazyRow` with `rememberLazyListState` and smooth scrolling upon arrow clicks, or item width proportioned so 4 items fit nicely without overcrowding).
  - Left arrow button on the left (RTL: end).
- Selection Styling:
  - "Today" / Selected day: Light purple background (`#EDE8FF` or `#E0D7FE`) with `#5B2CFF` or `#4C1D95` text and subtle purple outline, giving it a soft, airy feel instead of dark solid purple.
  - Unselected days: Clean neutral with subtle text.
- Dividers: Subtle vertical dividers between days.

## 2. Session Cards (`PixelPerfectSessionCard`)
- Border radius: Reduced from `24.dp` to `16.dp`.
- Padding & Height: Compact vertical padding (`10.dp - 12.dp`) instead of `16.dp`.
- Action buttons:
  - Remove Persian texts «ویرایش» and «حذف».
  - Only show clean icon buttons:
    - Edit: Soft lavender square/circle container with purple pen icon (`Icons.Outlined.Edit`).
    - Delete: Red tinted or soft neutral button with red trash icon (`Icons.Outlined.DeleteOutline`).
- Typography:
  - Subject title: `14.5.sp` (was 16sp), Bold.
  - Chapter subtitle: `12.sp` (was 13sp), Normal.
  - Duration & Time badges: `10.5.sp` (was 11.5sp), Medium.

## 3. Daily Summary Card (`PixelPerfectDailySummaryCard`)
- Border radius: `16.dp` (was 24dp).
- Vertical padding: `12.dp` (was 18dp).
- Font sizes:
  - Values: `17.sp` (was 20sp).
  - Labels: `11.sp` (was 11.5sp).
  - Units: `10.5.sp`.

## 4. Add Session Button (`AddSessionDashedButton`)
- Border radius: `16.dp` (was 20dp).
- Height: `48.dp` (was 56dp).
- Dashed stroke: Finer dots/dashes: `dashPathEffect(floatArrayOf(5f, 5f), 0f)` with stroke width `1.dp` (was `1.4dp` and `12f, 10f`).
- Font size: `13.5.sp` (was 14.5sp).
