# Technical Design: League Vector Asset Integration & Home Layout Polish

## 1. Asset & Image Management
- Copy `/league-homepage-vector.png` to `/app/src/main/res/drawable/ic_league_homepage_vector.png`.
- In `FeatureCardLeague`, update the `Image` painter from `painterResource(R.drawable.league)` to `painterResource(R.drawable.ic_league_homepage_vector)`.

## 2. Layout & Dimension Adjustments in `ReferenceHomeDashboard.kt`
1. **Top Grid Container**:
   - Change height from `256.dp` to `228.dp` (or `224.dp`), lifting the bottom row ("آزمون‌ساز" / "پرسش از همکلاسی‌ها") up by ~30dp.
2. **Smart Planner 3D Illustration (`FeatureCardSmartPlan`)**:
   - Keep/enhance image scaling with `size(360.dp)`.
   - Adjust `offset(y = 10.dp)` (reduced from `28.dp`) to bring the target/dart visually closer to the title text above it.
3. **CTA & Stacking**:
   - Maintain the white pill button at `Alignment.BottomStart` with arrow on the right.

## 3. Testing Matrix
- `HomeScreenTest.kt`: Verify all dashboard cards, texts ("آزمون‌ساز", "لیگ‌های رقابتی", "برنامه‌ریز هوشمند شتاب"), and actions remain fully rendered and clickable.
