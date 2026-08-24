# Actionable Tasks: Chapter Dropdown RTL, Inline Positioning & Border Refinements

## 1. Chapter Inline Dropdown
- [x] Replace `DropdownMenu` popup with an inline expandable `Surface` placed directly beneath the chapter selection field inside `ChapterTopicBlockItem`.
- [x] Ensure strict RTL orientation (`LayoutDirection.Rtl`), right text alignment, and proper spacing.
- [x] Eliminate the gray popup border and apply clean subtle borders matching the app design system.
- [x] Set max height to bounded 180dp–190dp (~25-30% height) with an internal `LazyColumn` for scrollable chapters.
- [x] Animate arrow icon (up/down) when expanded/collapsed.

## 2. Verification & Build
- [x] Verify unit & UI tests.
- [x] Run `compile_applet` to ensure clean build.
