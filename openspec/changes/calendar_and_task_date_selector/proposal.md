# Feature Proposal: Tasks Calendar Grid & New Task Date Picker & Profile Arrow Verification

## 1. Description & Context
This proposal covers the following improvements to Shetab:
1. **Profile Screen Navigation Arrows**:
   - Ensure all chevron/navigation arrows in Profile options (`PersonalInfoRow`, `AccountActionCard`) explicitly use left-pointing arrows (`Icons.AutoMirrored.Filled.KeyboardArrowLeft` or direct left arrows) with proper RTL layout rendering.
2. **Tasks Screen Calendar Modal / Dropdown**:
   - In the Tasks (`StudyPlanScreen`), clicking on the date / today selector opens a full, interactive Persian Monthly Calendar dialog/modal with:
     - Header displaying the current Month & Year (e.g. فروردین ۱۴۰۳) with previous/next month navigation arrows.
     - Day of week header row in Persian (ش, ی, د, س, چ, پ, ج).
     - Full grid of calendar days for that Jalali month with empty/padding slots for preceding days.
     - Highlighted styles for Today, Selected Day, and active task count dots or markers.
     - Fast Year/Month switching and a "امروز" (Go to Today) quick button.
3. **Date Selector in New Task Creation**:
   - In `CreateStudyPlanScreen` (New Task / Study Plan creation), add a clean, dedicated Date Selector component at the very top of the form (before grade/subject selection).
   - Allows students to select the scheduled date for their study task (Today, Tomorrow, or pick any custom Jalali date via the interactive date picker dialog).
   - Injects the selected date directly into `CreateManualStudyTaskDto` payload instead of hardcoding to today.

## 2. Acceptance Criteria
- **Profile Screen**: All row arrows point to the left in RTL orientation.
- **Tasks Screen**: Clicking on the date header launches an elegant monthly grid Persian calendar picker matching the provided design. Selecting a day updates the tasks view for that date immediately.
- **New Task Creation**: Top date selector is prominently visible, allows selecting today/tomorrow/custom date with full Jalali date picker integration, and properly schedules tasks on the chosen date.
- **Performance & Stability**: Zero unnecessary recompositions, smooth dialog animations, robust Persian date calculations.
