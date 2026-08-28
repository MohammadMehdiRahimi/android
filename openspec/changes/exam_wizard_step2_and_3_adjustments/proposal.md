# Proposal: Remove Step 2 Top Summary, Refactor Step 3 General Summary Grid (2x3), and Refine Book Card Question Badge

## 1. Overview
This change addresses user requests for refining the Exam Wizard Steps 2 and 3:
1. Remove the top summary text/section from Step 2 (`Step2QuestionSettingsScreen.kt`).
2. Refactor the "خلاصه کلی آزمون" (General Exam Summary) card in Step 3 (`Step3ExamSummaryScreen.kt`) by removing "پایه" (Grade) and "رشته" (Field), reorganizing the remaining 6 items into two rows of 3 columns (2x3 grid).
3. In each book card of Step 3, remove the "تعداد کل" label and only display the question count (e.g., `۱۴ سوال`) in black color with a small font.

---

## 2. Acceptance Criteria
1. **Step 2 (تنظیم سوالات)**:
   - The top summary header area is completely removed so the screen immediately begins with the questions setting configuration.
2. **Step 3 (خلاصه کلی آزمون)**:
   - "پایه" and "رشته" metric boxes are removed.
   - The remaining 6 attributes are organized in a 2-row by 3-column layout:
     - Row 1: نوع آزمون | تعداد کتاب‌ها | منبع سوال
     - Row 2: نمره منفی | چینش سوالات | مدت زمان تقریبی
3. **Step 3 Book Cards**:
   - The top-left badge removes the secondary label "تعداد کل".
   - The number (e.g. `۱۴ سوال`) is rendered in black (`Color.Black` / `colors.primaryText`) with small Persian typography (`11.sp` - `12.sp`).
