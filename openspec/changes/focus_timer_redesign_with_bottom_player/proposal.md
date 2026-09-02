# Proposal: Focus Timer Screen Visual Redesign & Integrated Audio Player

## 1. Context & Motivation
The user provided a target UI design image for the Focus & Study Timer screen (`FocusTimerScreen.kt`). The screen requires a complete visual refinement to match the reference image exactly, including:
1. **Top Header & Card:** Top bar with back button and centered title ("تمرکز و مطالعه"), followed by a rounded card featuring the subject title (e.g. "فیزیک ۳ – دینامیک و قوانین حرکت 📖") and a visual Pomodoro round/break stepper with progress lines and status tags.
2. **Precision Circular Dial with Ticks:** A circular progress gauge with tick marks, a modern gradient progression arc with an illuminated indicator knob, central brain icon, current round label ("مطالعه – دور ۱"), large Persian numbers ("۰۰:۰۵"), and a "زمان سپری‌شده" pill badge.
3. **Curved Control Buttons (3 Action Buttons):**
   - Reset button on the right: "بازنشانی" (circular white card with purple icon).
   - Primary Hero toggle in center: "شروع / توقف" (large white elevated button with purple play icon).
   - Fast-forward / skip on the left: "رد کردن این دور" (circular white card with pink/red fast-forward icon).
4. **Professional Bottom Player Bar:** A comprehensive ambient music player docked at the bottom with:
   - Album artwork thumbnail with soft rounded corners.
   - Track title ("موسیقی تمرکز") and subtitle ("صدای طبیعت و موج آرام").
   - Track progress slider with elapsed and total duration stamps.
   - Complete playback controls: Repeat/Loop, Stop/Pause, Previous Track, Next Track.

## 2. Acceptance Criteria
- [ ] Top card accurately displays the study task title and a 3-step Pomodoro cycle timeline with active ("در حال اجرا") and pending ("در انتظار") states and break intervals.
- [ ] Circular timer matches the reference visual with radial tick marks, purple arc progress, glowing knob, brain icon, and Persian digits.
- [ ] Action buttons (Reset, Play/Pause, Skip) are positioned along a curved arc with exact icons, labels, and colors.
- [ ] Integrated bottom player bar displays current track art, track titles, interactive progress bar, and player control actions.
- [ ] Strict RTL layout, Persian numerals, and typography aligned with `project.md` standards.
