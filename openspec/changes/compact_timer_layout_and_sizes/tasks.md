# Tasks Checklist: Compact Timer Layout & Scaled Controls

- [x] **1. Top Progress Box Compaction:**
  - [x] Update `PomodoroCycleProgress` component:
    - Reduce circle badge dimensions (active: 36.dp, pending/done: 30.dp).
    - Compact connector line height and text label sizes.
  - [x] Reduce padding and vertical spacing in top info card container in `FocusTimerScreen.kt`.

- [x] **2. Spacing Optimization:**
  - [x] Reduce vertical spacing between circular timer and curved action buttons.
  - [x] Optimize timer dial height and padding to produce a compact, well-proportioned aesthetic.

- [x] **3. Action Buttons & Arc Scaling:**
  - [x] Scale down Hero Play/Pause button from 64.dp to 54.dp (and icon from 34.dp to 28.dp).
  - [x] Scale down Reset and Skip buttons from 48.dp to 40.dp (and icons from 24.dp to 20.dp).
  - [x] Recalibrate parabolic arc track curve to match the new button sizes and positions.

- [x] **4. Verification & Testing:**
  - [x] Run `compile_applet` to confirm successful build.
