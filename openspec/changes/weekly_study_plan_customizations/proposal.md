# Proposal: Weekly Study Plan UI Refinements & Session Type Selector

## 1. Overview
This proposal defines four targeted refinements to the Study Plan module based on user feedback:
1. **Centered "Today" in Week Days Selector:** Instead of a static Saturday-anchored week where Thursday or Friday is pushed to the edge, the week day selector centers today's date dynamically (e.g. showing 3 days before today, today in the center, and 3 days after today).
2. **Swap Summary Card Metrics:** Swap the positions of "کل زمان مطالعه" (Total Study Time) and "تعداد جلسات" (Total Sessions Count) in the daily summary card.
3. **Remove Start Time Pill from Session Cards:** Remove the start time badge (e.g. "۱۱:۴۵") below the subject and chapter on session cards, keeping only the clean duration pill (e.g. "۹۰ دقیقه").
4. **Session Type Selector in Add Session Modal:** Add a dedicated "نوع جلسه" (Session Type) selection field directly above the scheduling section ("زمان‌بندی و دوره‌های مطالعه") in the Add Session modal, featuring four options:
   - **آزمون** (Exam / Test)
   - **آموزش** (Learning / Training)
   - **مرور** (Review)
   - **سایر** (Other)

---

## 2. Why (Motivation & Value)
* **Improved Ergonomics:** Persian users checking their weekly schedule need immediate access to *today's* context. Placing today in the center with adjacent past and future days provides optimal visual balance and effortless navigation.
* **Metric Hierarchy:** Placing session count first or swapping cards aligns with user scanning preference for daily task checklists.
* **Visual Clarity:** Removing redundant exact clock times (e.g. 11:45) removes visual clutter from the compact cards, keeping focus on the study duration and topic.
* **Pedagogical Classification:** Differentiating sessions between testing (آزمون), learning new concepts (آموزش), revision (مرور), and miscellaneous (سایر) helps students categorize their workload effectively.

---

## 3. Acceptance Criteria
1. **Center Today in Week Selector:**
   - For any selected/current date, the 7-day strip is generated with today/selected date at the center index (`-3..+3`).
   - The horizontal `LazyRow` maintains smooth scrollability with today visually centered on initial load.
2. **Summary Card Position Swap:**
   - In `PixelPerfectDailySummaryCard`, "تعداد جلسات" (Total Sessions Count) appears on the first side (right in RTL), and "کل زمان مطالعه" (Total Study Time) appears on the second side (left in RTL).
3. **Session Card Time Removal:**
   - In `PixelPerfectSessionCard`, the start time pill (e.g. "۱۱:۴۵", "۰۸:۳۰") is completely removed.
   - The duration badge (e.g. "۹۰ دقیقه") remains intact with crisp styling.
4. **Session Type Selection in Add Session Sheet:**
   - Directly above "زمان‌بندی و دوره‌های مطالعه", a new "نوع جلسه" section is rendered.
   - Four distinct selectable chips/tabs: آزمون (Exam), آموزش (Learning), مرور (Review), سایر (Other).
   - The selected type is tracked in state and reflected when saving or editing the session.
5. **Quality & Tests:**
   - All Robolectric unit and UI tests pass green without regressions.
