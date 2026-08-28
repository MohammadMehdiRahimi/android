# Tasks Checklist: League Vector Asset Integration & Home Layout Polish

- [x] **1. Asset Integration**
  - [x] Copy `/league-homepage-vector.png` to `/app/src/main/res/drawable/ic_league_homepage_vector.png`.
  - [x] Update `FeatureCardLeague` to use `painterResource(R.drawable.ic_league_homepage_vector)`.

- [x] **2. Presentation Layer Adjustments (`ReferenceHomeDashboard.kt`)**
  - [x] Reduce top grid height from `256.dp` to `226.dp` to bring "آزمون‌ساز" higher up.
  - [x] Adjust `FeatureCardSmartPlan` image offset and size so it sits closer to the top title.

- [x] **3. Verification & Testing**
  - [x] Run `compile_applet` to confirm build.
  - [x] Verify test suite in `HomeScreenTest.kt`.
