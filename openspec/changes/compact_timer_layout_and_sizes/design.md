# Design Specification: Compact Timer Layout & Scaled Controls

## 1. Top Progress Box Scaling (باکس پیشرفت بالا)
- **Container:**
  - Reduce vertical content padding from `16.dp` to `10.dp`.
  - Reduce inner vertical spacing between title and stepper from `16.dp` to `10.dp`.
- **Pomodoro Stepper (`PomodoroCycleProgress`):**
  - Scale active step circle badge from `44.dp` down to `36.dp` (font size `14.sp`).
  - Scale completed and pending step circle badges from `38.dp` down to `30.dp` (font size `13.sp`).
  - Reduce step label font sizes (Round name: `11.sp`, status label: `10.sp`).
  - Shorten connector line height from `16.dp` down to `12.dp` and node dot to `4.dp`.

## 2. Spacing Between Timer Dial and Action Buttons (کاهش فاصله تایم و دکمه‌ها)
- **Dial Box & Container Spacing:**
  - Reduce vertical padding on the circular dial container.
  - Adjust the parent Column vertical spacing or outer padding around the dial from `Arrangement.spacedBy(16.dp)` / `20.dp` down to `8.dp` or compact offset.
  - Decrease the dial height slightly if necessary (e.g. from `250.dp` to `230.dp`) to bring the timer numerals closer to the controls.

## 3. Scaled Action Controls & Arc Geometry (کوچک‌تر شدن سایز دکمه‌ها)
- **Button Dimensions:**
  - **Hero Play/Pause (Center):**
    - Size: scaled down from `64.dp` to `54.dp`.
    - Icon size: scaled from `34.dp` to `28.dp`.
    - Text size: scaled from `13.sp` to `11.5.sp`.
    - Y offset: adjusted from `16.dp` to `10.dp`.
  - **Reset (Right in RTL):**
    - Size: scaled down from `48.dp` to `40.dp`.
    - Icon size: scaled from `24.dp` to `20.dp`.
    - Text size: `11.sp`.
    - Y offset: adjusted from `-10.dp` to `-6.dp`.
  - **Skip Round (Left in RTL):**
    - Size: scaled down from `48.dp` to `40.dp`.
    - Icon size: scaled from `24.dp` to `20.dp`.
    - Text size: `11.sp`.
    - Y offset: adjusted from `-10.dp` to `-6.dp`.
- **Arc Track Line Calibration:**
  - Canvas height: reduced from `105.dp` to `80.dp`.
  - Start/End Y: adjusted to `20.dp.toPx()`.
  - Control Y: adjusted to `52.dp.toPx()`, maintaining smooth mathematical curvature directly through button centers.
