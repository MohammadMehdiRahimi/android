# Design: Weekly Study Plan UI Refinements & Session Type Selector

## 1. Architectural Architecture & Alignment with project.md
This change conforms strictly to the Clean Architecture and Unidirectional Data Flow (UDF) conventions outlined in `openspec/project.md`.

* **Layer Affected:** `ui/features/studyplan/`
* **Components Modified:**
  - `CreateStudyPlanViewModel.kt`: Day generation logic around today's anchor (`-3..+3`), session type model definition.
  - `PixelPerfectPlanComponents.kt`:
    - `PixelPerfectWeekSelector`: Scroll and initial state centering for today.
    - `PixelPerfectDailySummaryCard`: Swapping columns of Sessions Count vs. Study Time.
    - `PixelPerfectSessionCard`: Removal of start time pill.
    - `AddStudySessionModal`: Addition of "نوع جلسه" selector above scheduling section.

---

## 2. Component Design Details

### A. Centered Today in Week Selector
* **Algorithm:**
  In `CreateStudyPlanViewModel.generateWeekDaysForDate(selected: JalaliDate)`:
  Instead of anchoring from Saturday (`saturday = selected - weekdayIdx`), generate days centered on `selected`:
  ```kotlin
  val centerGregorian = selected.toGregorian()
  return (-3..3).map { dayOffset ->
      val dayGregorian = centerGregorian.plusDays(dayOffset.toLong())
      val jalali = JalaliDate.fromGregorian(dayGregorian)
      val dayName = DateTransformer.getPersianDayOfWeekName(dayGregorian)
      WeekDayItem(
          dayOfWeekName = dayName,
          dayOfMonth = jalali.day,
          monthName = jalali.monthName,
          date = jalali,
          isSelected = jalali == selected,
      )
  }
  ```
* **UI Behavior:**
  In `PixelPerfectWeekSelector`, today/selected day sits at index 3. In the 4-visible-items viewport or with initial scroll position, the view will ensure today is placed in the center of the viewport smoothly using `LaunchedEffect(selectedDate)` or initial scroll state.

---

### B. Daily Summary Card Metric Swap
* **Layout Structure (RTL):**
  - Section A (Right in RTL): **تعداد جلسات** (Total Sessions) with `Icons.AutoMirrored.Outlined.FormatListBulleted`, number, and "جلسه" unit.
  - Vertical Divider (1.dp, color `#F0F2F6`).
  - Section B (Left in RTL): **کل زمان مطالعه** (Total Study Time) with `Icons.Outlined.AccessTime`, hours formatted in Persian, and "ساعت" unit.

---

### C. Session Card Badge Cleanup
* **Change in `PixelPerfectSessionCard`:**
  - Locate the row containing the start time pill and duration pill.
  - Remove the start time badge (`session.startTime`).
  - Retain only the duration pill (e.g. `Surface(shape = RoundedCornerShape(6.dp), color = PlanBadgeBg) { Text(session.durationMinutes.toPersianNumber() + " دقیقه") }`).

---

### D. Session Type Selector in Add Session Modal
* **Domain / UI State:**
  Define enum or sealed class:
  ```kotlin
  enum class StudySessionType(val title: String) {
      EXAM("آزمون"),
      LEARNING("آموزش"),
      REVIEW("مرور"),
      OTHER("سایر"),
  }
  ```
* **Add Session UI (`AddStudySessionModal`):**
  Directly above:
  ```kotlin
  // --- 4. Schedule & Cycles Section ---
  ```
  Add:
  ```kotlin
  // --- Session Type Section ("نوع جلسه") ---
  Text(text = "نوع جلسه", ...)
  Row of 4 selectable chips (آزمون، آموزش، مرور، سایر)
  ```
  When tapped:
  - Updates `selectedSessionType` state.
  - Selected chip styled with brand purple container (`#EDE8FF`), purple text, and border.
  - Passes session type to `onConfirmAdd`.

---

## 3. Localization & RTL Compliance
* All labels externalized or mapped to standard Persian terms: "نوع جلسه", "آزمون", "آموزش", "مرور", "سایر".
* Layout direction strictly honors RTL without `left`/`right` modifiers.
* Iranian Sans / Vazirmatn typography utilized.
