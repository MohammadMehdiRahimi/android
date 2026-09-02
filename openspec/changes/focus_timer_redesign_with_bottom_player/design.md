# Design Specification: Focus Timer UI Redesign with Integrated Bottom Player

## 1. UI Hierarchy & Layout Composition (RTL First)

The layout is arranged vertically within a `Scaffold` or root `Column` with soft ambient background (`#FAFAFD`):

### A. App Bar
- Rounded Back Button on top start (RTL right side: `Modifier.align(Alignment.CenterEnd)` or custom row).
- Center Title: "تمرکز و مطالعه" (`IranSansFontFamily`, bold, `#1E1B4B`).

### B. Study Session Info Card (Top Card)
- Elevated/outlined rounded surface (`RoundedCornerShape(24.dp)`), white background, subtle border (`#F1F5F9`).
- Task Title Header: dynamically populated from current study task (e.g. "فیزیک ۳ – دینامیک و قوانین حرکت 📖") with Iranian typography.
- Pomodoro Stepper Row:
  - Round 1 (`دور ۱`): Filled purple badge with white Persian numeral "۱", title "دور ۱", subtitle "در حال اجرا" (`#6366F1`).
  - Connector: Line with "استراحت" above and "۵:۰۰" below. Active connector colored `#818CF8`.
  - Round 2 (`دور ۲`): Outline gray badge with numeral "۲", title "دور ۲", subtitle "در انتظار".
  - Connector: Inactive gray line with "استراحت" and "۵:۰۰".
  - Round 3 (`دور ۳`): Outline gray badge with numeral "۳", title "دور ۳", subtitle "در انتظار".

### C. Precision Circular Dial & Arc
- Canvas-based gauge:
  - Outer radial tick marks (60 calibration lines around the circumference, subtle `#E2E8F0` tint).
  - Background track circle (`#F3E8FF`).
  - Active progress arc with smooth sweep angle and end knob (inner dot + outer semi-transparent halo).
- Internal Content:
  - Brain/Psychology icon in soft purple.
  - Subtitle: "مطالعه – دور ۱".
  - Main Digital Display: "۰۰:۰۵" in large bold Persian numerals (`FarsiDigitConverter` formatting).
  - Badge: "زمان سپری‌شده" pill (`#F3E8FF` background, `#7C3AED` text). When overtime, adapts to green per previous feature.

### D. Action Controls (3-Button Curved Layout)
- Background faint curved dashed line aligning through button centers.
- Three buttons (RTL order: Right to Left):
  1. **Right:** `بازنشانی` – Circular button (48dp), white with shadow, purple circular arrow, label "بازنشانی".
  2. **Center:** `شروع` – Large Hero button (64dp), white with elevated shadow, bold purple Play/Pause triangle icon, label "شروع" / "توقف".
  3. **Left:** `رد کردن این دور` – Circular button (48dp), white with shadow, pink/red fast-forward icon, label "رد کردن این دور" in pink/red.

### E. Bottom Audio Player Card
- Rounded card (`RoundedCornerShape(24.dp)`) docked at bottom with white background, soft shadow, and border (`#F1F5F9`).
- Layout:
  - Cover Art: 54x54dp rounded image (nature/calm landscape image from existing assets or generated drawable).
  - Track Info: "موسیقی تمرکز" (bold, 13sp), subtitle "صدای طبیعت و موج آرام" (gray, 11sp).
  - Progress Row: Current time ("۰۸:۴۳"), interactive/animated slider bar with thumb knob, total duration ("۲۵:۰۰").
  - Player Controls Row:
    - Repeat / Loop mode button (`Icons.Outlined.Repeat` or `RestartAlt`).
    - Stop / Pause button (solid purple square/pause).
    - Previous track (`Icons.Filled.SkipPrevious`).
    - Next track (`Icons.Filled.SkipNext`).
- Integrates seamlessly with the app's sound synthesizer/media player state so audio can be started, paused, or switched directly from this bar.
