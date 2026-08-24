# Proposal: Refinement of Create Study Plan Screen & Catalog API Integration

## 1. Overview
This proposal defines the visual and architectural refinements for the **Create Study Plan** screen (`CreateStudyPlanScreen`) and the strict integration with the live `/study-tasks/me/catalog` API based on the user's latest design specifications and mockups.

## 2. Requirements & Acceptance Criteria

### Requirement 1: Top Navigation Bar
- The top bar in `CreateStudyPlanScreen` must have only one back button (pointing back to `StudyPlanScreen` / task list) placed on the right side in RTL.
- The forward circular button on the left must be completely removed.

### Requirement 2: Topic Chips Sizing & Typography
- The topic chips must have a more compact, refined height (36–38dp), reduced horizontal/vertical padding, and a smaller font size (11sp) to ensure high readability and sleekness.

### Requirement 3: Add Chapter Action Button
- Replace the bottom "X مورد انتخاب شده" count text in the Chapter/Topic card with a prominent full-width action button labeled **"اضافه کردن فصل"** with an add icon, allowing the student to add chapters/topics or open the chapter picker.

### Requirement 4: Pixel-Perfect Timing Section (Matching Attached Design)
- **Header:**
  - Right: Clock icon + "زمان‌بندی" title.
  - Left: "استفاده از زمان‌بندی پیش‌فرض" text + Switch toggle.
- **Divider:** Vertical dashed/dotted divider separating study duration and break duration.
- **Right Column (Study Time in RTL):**
  - Header: Book icon + "زمان مطالعه".
  - Value: "۴۵ دقیقه" (or selected minutes) in bold purple.
  - Slider: Force LTR direction with custom styled thumb (purple with center hole/stroke) and discrete step labels: `۱۵`, `۳۰`, `۶۰`, `۹۰`.
- **Left Column (Break Time in RTL):**
  - Header: Coffee/Tea Cup icon + "استراحت".
  - Value: "۱۵ دقیقه" (or selected minutes) in bold purple.
  - Slider: Force LTR direction with custom styled thumb and discrete step labels: `۵`, `۱۰`, `۱۵`, `۳۰`.

### Requirement 5: Live Catalog API Integration (`GET /study-tasks/me/catalog`)
- Remove static/mock data for books, chapters, and topics.
- Strictly call `GET /study-tasks/me/catalog` with `Authorization: Bearer <access-token>`.
- The server automatically returns only books matching the student's academic profile (`grade` and `fieldOfStudy`), with chapters and topics ordered ASC.
- Handle 409 Conflict (`ACADEMIC_PROFILE_INCOMPLETE`) gracefully if profile is incomplete.
