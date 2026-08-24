# Proposal: Create Study Plan Page Refinements (Header Spacing, Text-Only Book Chips, Overlay Chapter Dropdown with Numbering, Compact Timing Switch, Zero-Default Topics)

## 1. Overview & Problem Statement
User requested 5 specific UX/UI adjustments on the "Create Study Plan" (`CreateStudyPlanScreen` & `CreateStudyPlanViewModel`):
1. **Move Header Higher:** Reduce top spacing/padding for the "ایجاد برنامه مطالعاتی" header so it sits cleanly and comfortably higher up.
2. **Text-Only Book Selection Boxes (No Images):** Replace the current book cover cards with sleek, modern text-only selection chips/boxes displaying book names without images.
3. **Floating Overlay Dropdown for Chapters (No Layout Height Push) + Chapter Numbering:**
   - The chapter selection dropdown must open as a floating overlay positioned right over the trigger area without expanding or shifting the rest of the parent screen layout height.
   - Number chapters clearly in Persian (e.g. "فصل ۱: ...", "فصل ۲: ...").
4. **Compact Timing Toggle / Trigger Switch:** Shrink the manual timing toggle / trigger button size and height to make it more subtle and compact.
5. **No Default Topic Pre-selection on Chapter Change:** When a chapter is selected, `selectedTopicIds` must remain completely empty (`emptySet()`) instead of auto-selecting topics, allowing users to choose intentionally.

## 2. Acceptance Criteria
- [ ] Header title has reduced top padding, sitting higher on the screen.
- [ ] Subject/Book selection is styled as compact, modern, image-free choice chips/boxes.
- [ ] Chapter dropdown floats over the content (overlay/popup attached to position without increasing layout height) with Persian chapter numbers ("فصل ۱: ...").
- [ ] Timing trigger toggle has a reduced, compact height and footprint.
- [ ] Selecting a chapter initializes with zero pre-selected topics.
