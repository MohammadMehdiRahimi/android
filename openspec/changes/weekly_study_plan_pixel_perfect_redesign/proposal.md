# Proposal: Refine Weekly Study Plan UI (اصلاحات طراحی برنامه هفتگی)

## 1. Overview & Problem Statement
User feedback on the Weekly Study Plan screen (`CreateStudyPlanScreen` & `PixelPerfectPlanComponents`):
1. **Week Days Selector**:
   - Show only 4 days at a time, with smooth horizontal scrolling for the rest.
   - Position right arrow on the right, left arrow on the left of the scroll container.
   - Selected day / today color should be a soft/light purple (بنفش کمرنگ).
2. **Session Cards**:
   - More minimal design.
   - Remove the text "ویرایش" and "حذف" from each card, leaving only the clean icons.
   - Reduce card border radius (e.g., from 24dp to 16dp / 14dp).
   - Reduce the card height / vertical padding for a more compact and elegant look.
3. **Add Session Button**:
   - Make the dashed stroke dots finer/smaller (ریزتر شدن نقطه‌ها/خط‌چین).
4. **General Typography**:
   - Decrease font sizes overall across the components to make them cleaner and more compact.

## 2. Acceptance Criteria
- Week selector shows 4 days visible per viewport using a scrollable LazyRow or paging/scroll with right arrow on the right side and left arrow on the left side.
- Today/selected day pill is styled in soft/light purple (`#EDE9FE` / `#DDD6FE` or light purple background with dark purple text/accent).
- Session card actions display only the edit icon (pen) and delete icon (trash), without text labels.
- Card border radius reduced to 16dp (or 14dp).
- Card vertical padding reduced to make cards more compact.
- Dashed stroke on "Add Session" has finer dash pattern (e.g. `dashPathEffect(floatArrayOf(6f, 6f), 0f)` and thinner stroke width `1.dp`).
- Font sizes decreased harmoniously (Titles ~14.5sp, subtitles ~11sp, badges ~10.5sp, numbers ~17sp).
