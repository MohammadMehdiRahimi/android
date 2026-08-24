# Proposal: Refinement of Create Study Plan UI & Manual Task Creation API Integration

## 1. Overview
This proposal covers the comprehensive refinement of the **Create Study Plan** screen (`CreateStudyPlanScreen`), addressing UI design alignment with the provided screenshot, book name presentation, multi-chapter additions, and full integration of the `POST /study-tasks/me/manual` API according to the backend contract.

## 2. Requirements & Acceptance Criteria

### Requirement 1: Subtitle Removal
- Remove the text *"برنامه اختصاصی خود را در چند گام بساز"* from the top section of the screen.

### Requirement 2: Minimalist Book Names
- Simplify book names displayed in the book selection chips/cards to be sleek, clean, and minimal (removing redundant course numbers/prefixes where appropriate or presenting them in a clean pill format).

### Requirement 3: Contextual Dropdown for Chapter Selection
- The Chapter selection dropdown menu must open directly anchored below the Chapter selector box (`ExposedDropdownMenuBox` or inline anchored `DropdownMenu`) with matching app styling (rounded corners, clean shadow, RTL alignment, matching purple/slate color palette).

### Requirement 4: Chapter Selection Border
- Remove the green border from selected Chapters (use neutral/subtle purple styling instead).

### Requirement 5: Multiple Chapter & Topic Blocks
- Clicking "اضافه کردن فصل" must dynamically add a new Chapter & Topic section block below the existing one, allowing students to plan multiple chapters/topics within the study session.

### Requirement 6: Pixel-Perfect Slider Design Matching Provided Mockup
- Slider Track: Thick rounded capsule bar with active filled section, inactive faded section, and inner dots for tick steps.
- Slider Thumb: Distinct vertical pill / rounded indicator (vertical bar thumb in purple) sliding across the discrete steps.
- Sizing: Exact height and proportions matching the uploaded design image.

### Requirement 7: Conditional Manual Timing Toggle
- Change the header toggle label from *"استفاده از زمان‌بندی پیش‌فرض"* to **"زمان‌بندی دستی"**.
- By default, manual timing is disabled (the slider section is hidden).
- When toggled ON (زمان‌بندی دستی فعال), display the study & break sliders.

### Requirement 8: Manual Study Task API Integration (`POST /study-tasks/me/manual`)
- Endpoint: `POST /study-tasks/me/manual` with `Authorization: Bearer <access-token>`.
- Request Body (`CreateManualStudyTaskDto`):
  ```json
  {
    "requestId": "UUID v4",
    "topicId": "UUID",
    "scheduledOn": "YYYY-MM-DD",
    "periodCount": 3,
    "minutesPerPeriod": 45
  }
  ```
- Validation & Business Rules:
  - Generate a fresh UUID v4 `requestId` per submission.
  - Date `scheduledOn` within allowed window (today up to 30 days ahead).
  - Duration constraint: `periodCount * minutesPerPeriod <= 1440` minutes.
  - Graceful error handling:
    - 400 `MANUAL_TASK_DATE_OUTSIDE_ALLOWED_RANGE`
    - 400 `MANUAL_TASK_DURATION_TOO_LARGE`
    - 404 `MANUAL_TASK_TOPIC_NOT_AVAILABLE`
    - 409 `ACADEMIC_PROFILE_INCOMPLETE`
- On HTTP 201 Success: Display success toast/message and navigate back to the updated task list.
