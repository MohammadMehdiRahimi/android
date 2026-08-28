# Technical Design: Single-Line Card Titles & Enlarged Illustration

## 1. UI & Typography Specification

### 1.1 Single-Line Typography
- For cards with side illustrations (`FeatureCardLeague`, `FeatureCardStudyGroup`, `FeatureCardPeerTrouble`), currently the title text column has `.fillMaxWidth(0.64f)` and forced newline characters (`\n`).
- **Solution**:
  - Remove forced newlines `\n` in title strings.
  - Set `maxLines = 1` or remove width constraint to allow text to extend smoothly without forced multi-line wrapping.
  - Adjust z-ordering / stacking: The `Text` column is placed in the foreground with appropriate semi-transparent or crisp color so overlapping the background graphic looks intentional and readable.

### 1.2 Smart Planner Tall Card (`FeatureCardSmartPlan`)
- **Title**: `"برنامه‌ریز هوشمند شتاب"` (single line, `fontSize = 15.5.sp`, `maxLines = 1`, `overflow = TextOverflow.Ellipsis` or natural flow).
- **3D Graphic Scaling**:
  - Increase image modifier size from `130.dp` to `185.dp` - `200.dp`.
  - Align at `Alignment.BottomStart` with offset adjustments `offset(x = (-12).dp, y = (16).dp)` so the pedestal rests naturally at the card edge and the dart/target stands out prominently.

---

## 2. RTL & Material 3 Compliance
- All text remains right-aligned in RTL layout (`Alignment.Start` in RTL).
- Action buttons ("شروع کنید", arrow buttons) remain intact and accessible with standard minimum touch targets.
