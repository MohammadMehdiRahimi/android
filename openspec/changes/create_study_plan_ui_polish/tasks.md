# Actionable Tasks: Create Study Plan UI Polish & Inset Fixes

## 1. Layout & Inset Fixes
- [ ] Shift header ("ایجاد برنامه مطالعاتی") higher up by reducing top padding in `LazyColumn` and `CreatePlanTopBar`.
- [ ] Add `navigationBarsPadding()` to the Scaffold bottom bar surface to elevate "ثبت و ذخیره برنامه" above system navigation buttons.

## 2. Chapter Selector Dropdown / Dialog
- [ ] Replace wide full-screen menu overlay with a constrained, scrollable dropdown menu (`heightIn(max = 260.dp)`) with rounded corners and consistent padding.

## 3. Donut Thumb Slider
- [ ] Implement `DonutStepSlider` composable:
  - Donut Thumb (Circular ring with white center & purple stroke).
  - Background gray track & filled purple progress track.
  - Vertical step indicator tick marks.
  - Step mark labels in Persian digits (۵، ۱۰، ۱۵، ...) below the track.
- [ ] Integrate `DonutStepSlider` into Study Duration (15, 30, 45, 60, 90 min) and Break Duration (5, 10, 15, 20, 30 min).

## 4. Periods & Stepper Refinements
- [ ] Reduce stepper `+` and `-` circle buttons to 28.dp with refined padding and typography.

## 5. Topic Chips Refinements
- [ ] Update `TopicChipItem` to keep regular font weight and neutral border when selected, showing only the green checkmark badge (`#22C55E`).

## 6. Verification & Tests
- [ ] Update unit / UI tests in `CreateStudyPlanTest.kt`.
- [ ] Verify build with `compile_applet`.
