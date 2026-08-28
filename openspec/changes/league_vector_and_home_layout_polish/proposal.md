# Proposal: League Vector Asset Integration & Planner/Exam Layout Polish

## 1. Problem Statement & User Intent
The user requested three specific refinements:
1. **League Vector Integration**:
   - Use the newly uploaded `league-homepage-vector.png` from the root directory for the league card (`FeatureCardLeague`) in the Home Dashboard instead of `R.drawable.league`.
2. **Smart Planner Image Size & Text Spacing**:
   - Further enlarge the 3D target/dart illustration in the Smart Planner card to double size, ensuring it remains prominent and crisp.
   - Reduce the top gap/spacing between the subtitle text and the 3D illustration.
3. **Card Height Reduction & Exam Builder Lift**:
   - Reduce the overall height of the top grid section containing the Smart Planner and stacked cards (e.g., from `256.dp` to `220.dp` - `230.dp`), pulling the bottom section ("آزمون‌ساز" and "پرسش از همکلاسی‌ها") noticeably higher up on the dashboard.

---

## 2. Acceptance Criteria
1. `league-homepage-vector.png` is placed into `app/src/main/res/drawable/` as `ic_league_homepage_vector.png` (or replacing/updating the resource) and rendered in `FeatureCardLeague`.
2. The 3D illustration in `FeatureCardSmartPlan` is enlarged and vertically adjusted to sit closer to the title text.
3. The top section height is decreased, lifting "آزمون‌ساز" and "پرسش از همکلاسی‌ها" upwards.
4. All existing tests pass and the app compiles with zero errors.
