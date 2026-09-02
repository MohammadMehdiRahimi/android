# Proposal: Compact Focus Timer Layout and Scaled Controls

## 1. Context & User Request
The user requested three specific dimensional and spatial refinements on the Focus Timer screen:
1. **Reduce vertical gap between timer and bottom buttons:** The distance separating the circular timer dial and the curved action control buttons should be tighter/smaller.
2. **Reduce button sizes:** The action buttons (Reset, Play/Pause hero button, Skip/Finish button) should have smaller, more refined dimensions.
3. **Reduce top progress box size:** The top Pomodoro session progress card ("باکس پیشرفت بالا") should be more compact with smaller padding, tighter stepper elements, and reduced overall height.

## 2. Acceptance Criteria
- [ ] Top session info / Pomodoro progress card is made more compact (reduced padding from 16.dp to 10-12.dp, smaller circle badges, and tighter typography).
- [ ] Vertical distance between the central circular timer dial and the curved control buttons is significantly decreased (adjusting Column spacing/padding).
- [ ] Action buttons are scaled down:
  - Center Play/Pause Hero button reduced from 64.dp to 54-56.dp.
  - Side buttons (Reset and Skip) reduced from 48.dp to 40-42.dp with appropriately scaled icons and labels.
  - Arc track line height and curve adjusted to fit the smaller button dimensions seamlessly.
- [ ] Visual harmony and strict RTL support are preserved throughout the screen.
