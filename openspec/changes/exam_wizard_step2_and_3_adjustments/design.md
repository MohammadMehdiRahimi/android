# Design: Step 2 and Step 3 UI Refinements

## 1. Modifications to `Step2QuestionSettingsScreen.kt`
- Remove the top summary section (`Row` / `Column` showing نوع آزمون, پایه, رشته, etc.).
- Ensure vertical spacing and padding flow cleanly directly into the book question list.

## 2. Modifications to `Step3ExamSummaryScreen.kt`

### 2.1 General Summary Card Grid (2 Rows x 3 Columns)
The grid inside "خلاصه کلی آزمون" will contain 6 items arranged in 2 rows of 3 columns:
- **Row 1**:
  - Item 1: `نوع آزمون` (e.g. آزمون تستی)
  - Item 2: `تعداد کتاب‌ها` (e.g. ۲ کتاب)
  - Item 3: `منبع سوال` (e.g. تألیفی / کنکور)
- **Row 2**:
  - Item 4: `نمره منفی` (e.g. فعال / غیرفعال)
  - Item 5: `چینش سوالات` (e.g. تصادفی / ترتیبی)
  - Item 6: `مدت زمان تقریبی` (e.g. ۴۵ دقیقه)

Each item will occupy `Modifier.weight(1f)` for balanced horizontal alignment in RTL.

### 2.2 Exam Section Book Cards Question Badge
- In `ExamSectionBookItemCard`:
  - Replace the multi-line badge container with a clean, single-line surface or text element.
  - Remove `"تعداد کل"`.
  - Display `${config.totalCount.toString().toPersianNumber()} سوال` in black (`colors.primaryText` / `Color(0xFF111827)`) with small font size (`11.5.sp` - `12.sp`).
