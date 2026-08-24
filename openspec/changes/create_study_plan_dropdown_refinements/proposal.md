# Proposal: Chapter Dropdown RTL, Inline Positioning & Border Refinements

## 1. Overview & Problem Statement
Based on user feedback and review of the chapter selection dropdown:
1. **Remove Gray Outer Border:** The selector dropdown had an unintended gray border/shadow popup container artifact.
2. **Strict RTL Layout:** The dropdown content in the popup was left-aligned (LTR); it must be strictly RTL (right-aligned Persian text, correct padding and checkmark positions).
3. **Exact Inline Position & Bounded Height (Max ~30%):** The popup was expanding over the entire screen. It must be positioned directly under the "انتخاب فصل" selector box as an inline expandable accordion/dropdown with a bounded maximum height (around 180dp–200dp / ~25-30% height) with smooth vertical scrolling.

## 2. Acceptance Criteria
- [ ] Chapter selection opens cleanly right below the selector field inside the chapter block card.
- [ ] No gray system/popup outer border.
- [ ] Strict RTL layout with right-aligned Persian text (`IranSansFontFamily`, `TextAlign.Right` / `TextAlign.Start` in RTL).
- [ ] Bounded height (max 180dp–200dp) with smooth scrollable `LazyColumn`.
- [ ] Selecting a chapter chooses it, selects topics, and collapses the inline dropdown immediately.
