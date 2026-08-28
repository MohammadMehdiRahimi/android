# Technical Design: Dashboard Cleanup & Visual Refinements

## 1. Presentation Layer Specifications (`ReferenceHomeDashboard.kt`)

### 1.1 Header & Spacing Refinements
- **Spacing**: In the main `Column`, reduce vertical spacing between `HomeTopHeader` and `PerformanceChartCard` (e.g. from `10.dp` to `4.dp` or remove extra padding in `HomeTopHeader`).
- **Nickname Removal**: In `HomeTopHeader`, remove the `Text` composable that rendered `displayTitle` ("ناشگر برتر") below `displayName`.

### 1.2 Subtitles Removal in Feature Cards
- **`FeatureCardLeague`**:
  - Remove subtitle `Text("تو یک قدم تا جایزه")`.
  - Shift `Image` (ic_league_homepage_vector) leftward using `offset(x = 6.dp)` or `align(Alignment.CenterEnd)` with optimal sizing so it sits distinctly on the left edge.
- **`FeatureCardStudyGroup`**:
  - Remove subtitle `Text("با هم بهتر میتونیم")`.
- **`FeatureCardPeerTrouble`**:
  - Remove subtitle `Text("سوالت رو سریع پاسخ بگیر")`.
- **`FeatureCardExamBuilder`**:
  - Remove subtitle `Text("آزمون بساز و تمرین کن")`.

### 1.3 Smart Planner Image Scaling & Proximity
- **`FeatureCardSmartPlan`**:
  - Enlarge 3D image `size` to `420.dp` - `440.dp`.
  - Adjust vertical alignment & offset (`offset(y = 4.dp)`) so it sits directly below the top text with minimal gap.

---

## 2. Test Suite Adaptations
- Update `HomeScreenTest.kt` if any old subtitle strings were explicitly asserted.
