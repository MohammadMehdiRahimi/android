# Design: Create Study Plan UI Polish & Inset Fixes

## 1. Top Bar & Window Inset Layout Strategy
- **Top Bar:** Reduce vertical margins and use compact top padding in combination with `statusBarsPadding()` so the header sits cleanly at the top of the viewport.
- **Bottom Bar:** Apply `.navigationBarsPadding()` to the bottom surface container or scaffold bottom bar to prevent Android 3-button and gesture bars from overlaying the save button.

## 2. Chapter Selection Component
- Implement a modal/popover or bound `DropdownMenu` with strict maximum height (`heightIn(max = 260.dp)`), smooth vertical scroll, rounded corners (`16.dp`), subtle borders (`PlanCardBorder`), and clean Persian typography.
- Selection items will have subtle hover/tap feedback with a check icon for the active chapter.

## 3. Donut Thumb Slider Component (`DonutStepSlider`)
- **Canvas / Layout Structure:**
  - **Height:** 54.dp container with padding.
  - **Track:** 4.dp background bar with rounded ends in `#E2E8F0`.
  - **Filled Track:** 4.dp filled line in `#6C5CE7` from start to current step position.
  - **Step Ticks / Marks:** Vertical tick lines (height 6.dp, width 2.dp) placed at discrete step divisions along the track.
  - **Donut Thumb:** Outer circle diameter 20.dp with 3.5.dp border `#6C5CE7` and solid white fill (`Color.White`), with a soft drop shadow.
  - **Labels:** Persian numerals rendered directly below each corresponding tick mark.
  - **Touch & Drag Gestures:** Drag and tap gesture listeners snapping smoothly to discrete step values.

## 4. Study Periods Stepper
- Stepper button sizes reduced to `28.dp` with `14.dp` icon sizes and soft purple circular backgrounds (`#EDE9FE`).
- Center value clearly shows Persian digits with subtitle unit.

## 5. Topic Chip Selection Styling
- Unselected: Neutral background (`#F8F9FE`), neutral border (`#E2E8F0`), regular font weight, empty circle.
- Selected: Neutral background (`#F8F9FE`), neutral border (`#E2E8F0`), regular font weight, filled green circle (`#22C55E`) with white checkmark icon.
