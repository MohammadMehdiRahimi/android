# Proposal: Exam Builder Step 2 Redesign & Step 1 Inline Book Selection

## 1. Summary (خلاصه تغییرات)
This change implements the pixel-perfect redesign of **Step 2 (مرحله ۲: تنظیم سوالات)** strictly matching the provided reference image `exam-create-2.png`, and converts the book addition flow in **Step 1 (مرحله ۱: ساختار آزمون)** from a modal dialog into an **inline expandable selection box** within the page.

---

## 2. Motivation & Requirements (اهداف و نیازمندی‌ها)
- **Inline Book Addition in Step 1**: The user requested that adding books should NOT open a modal popup/dialog, but should render directly inline inside the main page as an expandable selection box (with book list, chapter dropdown, topic tags, and confirm button).
- **Pixel-Perfect Step 2 UI (`exam-create-2.png`)**:
  1. **Top Bar & Stepper**: Back arrow on left (in RTL), title `طراحی آزمون جدید` and subtitle `مرحله ۲: تنظیم سوالات`, help circular icon `?` on right. Stepper showing Step 1 completed (checkmark `✓`), Step 2 active (purple `۲`), and Step 3 pending (`۳`).
  2. **Top Config Summary Card**: 5-column metric card showing `نوع آزمون (آزمون تستی)`, `پایه (دهم)`, `رشته (ریاضی‌فیزیک)`, `تعداد کتاب‌ها (۲ کتاب)`, and `منبع سوال (تألیفی / کنکور)` separated by clean vertical dividers.
  3. **Per-Book Question Setting Cards**:
     - Right: Book cover artwork thumbnail, Book title (`ریاضی دهم`), Chapter subtitle (`فصل ۲: تابع`).
     - Left: Red trash delete action (`حذف` + trash icon).
     - Topics row: `موضوعات انتخابی:` with light purple chips (`تابع و نمودار`, `دامنه و برد`, `ترکیب توابع`).
     - Difficulty Counters: 4 difficulty levels (`آسان`, `متوسط`, `دشوار`, `خیلی دشوار`) each with interactive stepper buttons (`-`, count, `+`).
     - Footer: `جمع سوالات این بخش: ۱۴ سوال` with reactive total questions calculation.
  4. **Exam Options Card (`تنظیمات آزمون تستی`)**:
     - Right setting: `نمره منفی` with toggle switch and description `هر ۴ پاسخ غلط = ۱ پاسخ صحیح منفی`.
     - Left setting: `چینش تصادفی سوالات` with shuffle icon and toggle switch.
  5. **Bottom Action Buttons**:
     - Large purple primary button: `ادامه به مرحله بعد` with left arrow icon.
     - Text action button: `بازگشت` (navigates back to Step 1).

---

## 3. Acceptance Criteria (معیارهای پذیرش)
- [ ] Step 1 inline book addition opens directly inside the page without any `AlertDialog` or modal sheet.
- [ ] Step 2 top summary card correctly reflects all user selections from Step 1 with Persian numbers.
- [ ] Difficulty stepper counters for each selected book update difficulty counts and reactive sum (`جمع سوالات این بخش`).
- [ ] Deleting a book card removes it and recalculates the total.
- [ ] Exam settings toggles (Negative marking & Random question order) are functional and switchable.
- [ ] Full RTL alignment with Persian typography (IranSans font) and smooth navigation to Step 3 and back to Step 1.
