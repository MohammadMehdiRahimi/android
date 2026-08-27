# Design: Study Plan Styling Refinements

## Visual & Component Design

### 1. Start Button (`StudyTaskItemCard`)
- In `StudyPlanComponents.kt`:
  - Change the container of the "شروع" button from solid purple `PlanPurple` to `PlanPurpleLight` (`Color(0xFFEDE9FE)` or `Color(0xFFF3E8FF)`).
  - Change text and icon colors from `Color.White` to `PlanPurple` (`Color(0xFF7656F5)` or `Color(0xFF6D4CF3)`) for high-contrast accessibility on a soft pastel surface.

### 2. Subject Badge Box (`SubjectBadgeBox`)
- Remove the secondary label `gradeOrField` from `SubjectVisualConfig` data class and `SubjectBadgeBox` composable.
- Keep the clean icon + subject title ("ریاضی", "فیزیک", "شیمی", "زیست‌شناسی", etc.) cleanly vertically centered inside the rounded badge.

### 3. Bottom Navigation Purple Hue (`ShetabBottomNavigation`)
- Soften the bottom navigation purple from `0xFF7656F5` to `0xFF8B6EF7` / `0xFF8568F8` (lighter, softer, and modern M3 tone).
- Apply to:
  - Active item icon and label tints.
  - Floating center "Home" action button container and its shadow spot color.

## Persian & RTL Rules
- Layout direction remains strictly RTL.
- All Persian typography and paddings remain intact.
