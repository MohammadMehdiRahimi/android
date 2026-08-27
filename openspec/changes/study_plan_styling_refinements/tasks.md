# Tasks: Study Plan Styling Refinements

## 1. UI Styling Tasks
- [ ] Update `StudyTaskItemCard` in `StudyPlanComponents.kt`:
  - Set the "شروع" button container background to light purple (`PlanPurpleLight`).
  - Set the text and icon color of the "شروع" button to primary purple (`PlanPurple`).
- [ ] Update `SubjectVisualConfig` and `SubjectBadgeBox` in `StudyPlanComponents.kt`:
  - Remove `gradeOrField` property.
  - Remove rendering of "رشته تجربی" and ensure clean vertical alignment of subject title and icon.
- [ ] Update `ShetabBottomNavigation` in `MainScreen.kt`:
  - Soften active tab colors and floating center button color to a lighter, softer purple (`Color(0xFF8B6EF7)`).

## 2. Verification & Testing Tasks
- [ ] Verify build compilation via `compile_applet`.
- [ ] Run unit and Robolectric tests via `gradle :app:testDebugUnitTest`.
