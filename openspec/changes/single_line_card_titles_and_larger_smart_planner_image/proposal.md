# Proposal: Single-Line Card Titles & Enlarged Smart Planner 3D Illustration

## 1. Problem Statement & User Intent
The user requested two specific UI adjustments on the Home Dashboard:
1. **Single-Line Card Titles (عدم شکست خط عنوان کارت‌ها)**:
   - Ensure the titles for **لیگ‌های رقابتی**, **گروه‌های مطالعاتی من**, **برنامه‌ریز هوشمند شتاب**, and **پرسش از همکلاسی‌ها** are rendered on a single line without breaking into multiple lines (`\n`).
   - Remove narrow width constraints (`fillMaxWidth(0.64f)`) so text flows across the card, allowing natural overlap with background/side illustration assets if needed.
2. **Significantly Larger Smart Planner 3D Illustration (بزرگ‌تر کردن اندازه تصویر ۳بعدی سیبل و تیر)**:
   - Increase the 3D target and dart graphic size in `FeatureCardSmartPlan` considerably (e.g., from `130.dp` to `180.dp+`) so it matches the bold, prominent aesthetic of the reference artwork.

---

## 2. Scope of Changes
- **`ReferenceHomeDashboard.kt`**:
  - `FeatureCardSmartPlan`:
    - Set title to single line: `"برنامه‌ریز هوشمند شتاب"` or `"برنامه ریز هوشمند شتاب"`.
    - Significantly enlarge the 3D target image (`size(185.dp)` with optimal alignment/offset).
  - `FeatureCardLeague`:
    - Set title to single line: `"لیگ‌های رقابتی"`.
    - Expand title column width (`fillMaxWidth()`) so it does not wrap.
  - `FeatureCardStudyGroup`:
    - Set title to single line: `"گروه‌های مطالعاتی من"`.
    - Expand title column width.
  - `FeatureCardPeerTrouble`:
    - Set title to single line: `"پرسش از همکلاسی‌ها"`.
    - Expand title column width.
- **`HomeScreenTest.kt`**:
  - Update UI test assertions to verify single-line strings.

---

## 3. Acceptance Criteria
1. "لیگ‌های رقابتی" renders in a single line.
2. "گروه‌های مطالعاتی من" renders in a single line.
3. "پرسش از همکلاسی‌ها" renders in a single line.
4. "برنامه‌ریز هوشمند شتاب" renders in a single line.
5. The 3D target & dart illustration in the Smart Planner card is visibly much larger and well-balanced.
6. All automated unit & UI tests pass.
