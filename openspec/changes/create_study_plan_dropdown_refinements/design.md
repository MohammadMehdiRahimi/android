# Design: Chapter Dropdown RTL, Inline Positioning & Border Refinements

## 1. Chapter Inline Dropdown Architecture
- Replace the detached window `DropdownMenu` with an in-card inline expandable dropdown container positioned directly underneath the "انتخاب فصل" button.
- When `isChapterMenuOpen` is true:
  - Animate or render an inline `Surface` directly below the selector field.
  - Apply `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl)` or native RTL container alignment to guarantee right-to-left layout direction.
  - Max height constrained to `heightIn(max = 190.dp)` with a scrollable `LazyColumn`.
  - Border styled seamlessly with `PlanPurple.copy(alpha = 0.25f)` or subtle `Color(0xFFE2E8F0)` (no gray system popup artifact).
  - Background is crisp white (`Color.White`) with rounded corners (`14.dp`).
  - Items display chapter title aligned to the right with Persian typography (`IranSansFontFamily`, `12.5.sp`) and a clean checkmark indicator on selection.

## 2. Interaction & State
- Tapping the selector toggles the inline dropdown.
- Tapping any chapter updates `selectedChapterId`, auto-selects chapter topics, and closes the dropdown (`isChapterMenuOpen = false`).
- Keyboard arrow icon rotates smoothly between collapsed and expanded states.
