# Design: Create Study Plan Page Refinements

## 1. UI Refinements in `CreateStudyPlanScreen.kt`

### A. Header Positioning
- Adjust `Spacer(modifier = Modifier.height(...))` and top padding in the header `Row` so the title and back button are placed closer to the top status bar / insets boundary.

### B. Text-Only Book Selection Boxes
- Redesign `SubjectCardItem` / replace with `SubjectBoxItem`:
  - Eliminate the book cover image / placeholder box.
  - Render as sleek, modern horizontal pill / rounded rectangular chips (`RoundedCornerShape(12.dp)`).
  - Clean Persian typography, selected state with `PlanPurple` fill or accent border, unselected with neutral background (`Color(0xFFF8F9FE)`).
  - Compact height (~38dp - 44dp).

### C. Floating Overlay Chapter Dropdown with Numbering
- Use a floating Popup overlay anchored directly below the select box (`Popup(alignment = Alignment.TopStart, onDismissRequest = ...)` or a properly styled zero-system-border `DropdownMenu` with RTL provider and fixed width matching the trigger).
- Avoid expanding parent height layout.
- Format chapter titles with Persian numbering:
  - e.g., `"فصل ${index + 1}: ${ch.name.removePrefix("فصل ...")}"` or clean indexation `"فصل ۱: ..."` using Persian numeral formatting.
- Height constrained to `heightIn(max = 190.dp)` with scrollable `LazyColumn`.

### D. Compact Timing Switch / Trigger
- Reduce padding and dimensions of the manual timing switch container.
- Smaller font and icon dimensions for a neat, compact visual footprint.

## 2. State & ViewModel Refinements in `CreateStudyPlanViewModel.kt`
- In `selectChapterForBlock(blockId, chapterId)`:
  - Set `selectedTopicIds = emptySet()` instead of pre-selecting topics.
- Keep tests updated and validated.
