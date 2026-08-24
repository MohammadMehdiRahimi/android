# Design: Study Plan and Tasks Refinements (v4)

## 1. Architecture & Layout Alignment

### Tasks Screen (`ui/features/studyplan/StudyPlanComponents.kt` & `StudyPlanScreen.kt`)
1. **Top Header Simplification:**
   - Update `StudyPlanTopHeader`:
     - Remove avatar image box (`AsyncImage` / `Icons.Filled.Person`).
     - Remove notification bell button (`Icons.Outlined.Notifications`).
     - Remove subtitle `Text(text = stringResource(id = R.string.study_plan_subtitle))`.
     - Center the title `Text(text = stringResource(id = R.string.study_plan_title), ...)` with standard `statusBarsPadding()` and compact padding.
2. **FAB Placement:**
   - In `StudyPlanScreen.kt`, update `AddTaskFloatingActionButton`:
     - Set `modifier = Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = 16.dp)` so it sits cleanly and compactly directly above the bottom navigation bar without unnecessary offset.

---

### Create Study Plan Screen (`ui/features/studyplan/CreateStudyPlanScreen.kt`)
1. **Granular Skeleton Loaders for Book & Chapter Fetching:**
   - Instead of a full-screen loading state or blank spaces, show skeleton shimmer loaders specifically inside:
     - The Book Selection row (`LazyRow` of skeleton chips/boxes matching book card sizes).
     - The Chapter Selector card (skeleton rectangular box matching the chapter dropdown surface).
   - Ensure the rest of the screen (Top bar, Grade selector, Study Periods count, Manual Timing section, Save Plan button) remains rendered and visible.
2. **Top Bar Positioning:**
   - Place `CreatePlanTopBar` at the top edge with `Modifier.statusBarsPadding().padding(horizontal = 20.dp, vertical = 8.dp)`.
   - Ensure title "ایجاد برنامه مطالعاتی" is vertically centered with the back button and does not clip under the status bar notch/cutout.
3. **Compact Borderless Manual Timing Switch:**
   - In `TimingSection`, configure the `Switch` component:
     - Use `Modifier.scale(0.8f)` or custom compact dimensions.
     - Ensure container card and switch have no border stroke (`border = null` / `border = BorderStroke(0.dp, Color.Transparent)` or pure surface color).

---

## 2. RTL & Persian Typography
- Strict Right-to-Left layout adherence with IranSans font family.
- Numbers formatted via `toPersianNumber()`.
