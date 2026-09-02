# Tasks Checklist: Focus Timer Redesign & Bottom Player Implementation

- [x] **1. Presentation Layer - Top Header & Session Info Card:**
  - [x] Refactor top bar to feature centered title "تمرکز و مطالعه" and right-aligned back button.
  - [x] Implement the rounded session info card with task title and book icon ("فیزیک ۳ – دینامیک و قوانین حرکت 📖").
  - [x] Implement the multi-step Pomodoro cycle timeline (Rounds 1, 2, 3 with active/pending badges, progress connectors, and "استراحت ۵:۰۰" labels).

- [x] **2. Presentation Layer - Central Circular Dial with Radial Ticks:**
  - [x] Enhance Canvas to draw radial tick marks along the circumference.
  - [x] Draw active progress arc with glowing knob and end cap.
  - [x] Display central brain icon, "مطالعه – دور ۱" label, large Persian digital timer, and "زمان سپری‌شده" pill badge.

- [x] **3. Presentation Layer - 3-Button Curved Controls:**
  - [x] Reconfigure the curved controls row to match the 3-button layout:
    - Right: Reset button ("بازنشانی")
    - Center: Large Play/Pause Hero button ("شروع" / "توقف")
    - Left: Skip button ("رد کردن این دور" in pink/red with FastForward icon)
  - [x] Align arc track seamlessly behind the 3 buttons.

- [x] **4. Presentation & Audio Layer - Professional Bottom Player:**
  - [x] Build the bottom player card with album art, track title ("موسیقی تمرکز"), and subtitle ("صدای طبیعت و موج آرام").
  - [x] Add playback progress bar with Persian timestamps.
  - [x] Implement playback controls (Repeat, Stop/Play, Prev, Next) wired to sound engine/audio state.

- [x] **5. Verification & Testing:**
  - [x] Run `compile_applet` to confirm successful build with zero errors.
