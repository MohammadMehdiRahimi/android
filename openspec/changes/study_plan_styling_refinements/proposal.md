# Proposal: Study Plan UI Refinements: Start Button, Subject Badge, and Bottom Nav Palette

## Problem Statement
1. **Start Button (دکمه شروع) Color in Study Tasks:**
   In the study plan tasks screen (`StudyPlanComponents.kt`), the start button currently uses a solid saturated purple (`PlanPurple`), whereas the user requested a soft, light purple (بنفش کم‌رنگ).
2. **Subject Badge Field of Study Removal:**
   The task cards currently render "رشته تجربی" under the subject title. The user explicitly requested to completely remove "رشته تجربی" and not show any field of study string in the card badges.
3. **Bottom Navigation Purple Tone Refinement:**
   The primary purple used across `ShetabBottomNavigation` (`0xFF7656F5`) is slightly too bold/saturated. The user requested a slightly softer, lighter, and more delicate purple tone for the bottom navigation.

## Proposed Changes
1. **Start Action Button Styling (`StudyTaskItemCard`):**
   - Update the start button background to a soft, modern light purple tint (e.g. `Color(0xFFEDE9FE)` / `PlanPurpleLight` with `PlanPurple` text & icon, or soft lavender container).
2. **Remove Field of Study from Badges (`SubjectVisualConfig` & `SubjectBadgeBox`):**
   - Remove `gradeOrField` from `SubjectVisualConfig` and `SubjectBadgeBox` so no major or branch text is rendered.
   - Adjust vertical centering and padding of the subject icon and title.
3. **Refine Bottom Navigation Primary Purple (`ShetabBottomNavigation`):**
   - Soften `0xFF7656F5` to a smoother, more elegant pastel-friendly purple tone such as `Color(0xFF8B6EF7)` / `Color(0xFF8566F6)` for the active bottom bar icons, text, and central home bubble.

## Acceptance Criteria
- [x] The start button in study plan task cards uses a soft light purple tone.
- [x] No field/major text (e.g., "رشته تجربی") is displayed on the task cards.
- [x] The primary purple in the bottom navigation is refined to a softer, gentler shade.
