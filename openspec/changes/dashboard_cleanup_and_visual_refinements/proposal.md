# Proposal: Dashboard Visual Refinements & Cleanup

## 1. Problem Statement & User Intent
The user requested four specific visual refinements on the Home Dashboard:
1. **Smart Planner Image Size & Spacing (بزرگ‌تر شدن بیشتر تصویر برنامه‌ریز هوشمند)**:
   - Make the 3D target and dart illustration in `FeatureCardSmartPlan` even larger and prominently framed.
   - Reduce the top gap between the title and the 3D illustration.
2. **League Card Icon Position (انتقال آیکون لیگ به سمت چپ‌تر)**:
   - In Persian RTL, the left side is `Alignment.CenterEnd` or with a negative/leftward offset so the shield/trophy vector sits further to the left edge.
3. **Remove Subtitles Under Card Titles (حذف متن زیر عنوان‌ها)**:
   - Remove subtitle text in:
     - **گروه‌های مطالعاتی من** (Remove "با هم بهتر میتونیم")
     - **لیگ‌های رقابتی** (Remove "تو یک قدم تا جایزه")
     - **آزمون‌ساز** (Remove "آزمون بساز و تمرین کن")
     - **پرسش از همکلاسی‌ها** (Remove "سوالت رو سریع پاسخ بگیر")
4. **Header Spacing & Nickname Removal (کاهش فاصله هدر و حذف لقب کاربر)**:
   - Reduce vertical spacing between the user header and the performance chart.
   - Remove the user nickname/title (e.g. "ناشگر برتر") under the user's name in the header so only the user's name is displayed cleanly.

---

## 2. Acceptance Criteria
1. Subtitle text is removed from the 4 feature cards (`FeatureCardLeague`, `FeatureCardStudyGroup`, `FeatureCardExamBuilder`, `FeatureCardPeerTrouble`).
2. User nickname subtitle is removed from `HomeTopHeader`.
3. Vertical space between header and performance chart is reduced.
4. Smart Planner 3D graphic is visibly larger and closer to the header text.
5. League vector icon is shifted further to the left.
6. All automated UI tests pass and build compiles successfully.
