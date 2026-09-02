# Design: Study Groups RTL Refinement, Minimalist Layout & Typography Optimization

## 1. Architectural Strategy
Following the Feature-First Clean Architecture guidelines in `project.md`:
- Keep all models in `com.example.ui.features.mygroup.MyGroupModels.kt`.
- Update presentation components in `com.example.ui.features.mygroup.MyGroupComponents.kt`.
- Update screen orchestration and ViewModel binding in `com.example.ui.features.mygroup.MyGroupScreen.kt`.
- Update strings in `res/values/strings.xml`.
- Update tests in `MyGroupDualStateTest.kt`.

## 2. Component Design & Layout Adjustments

### 2.1 Top Bar
- Height: 52dp.
- Back button: 38dp circular icon button with `Icons.AutoMirrored.Filled.ArrowBack` or `ArrowForward` as required by RTL context.
- Title: 17sp Bold `IranSansFontFamily`, `#1E293B`.

### 2.2 Non-Member (State 1) - Minimalist Refinement
- **Search Bar:**
  - Height: 46dp, corner radius: 16dp.
  - RTL input direction with right-aligned placeholder and text (`TextAlign.Start`).
  - Search icon on the start side or end side naturally aligned in RTL.
- **Filter Chips:**
  - Compact height: 36dp, corner radius: 12dp.
  - Font size: 12sp, icon size: 14dp.
- **Empty Card:**
  - Illustration size: 160dp (reduced from 240dp for a clean minimal feel).
  - Title font size: 16sp Bold (down from 21sp).
  - Subtitle font size: 12sp (down from 14sp).
  - Buttons: 44dp height, corner radius: 12dp, font size: 13sp.

### 2.3 Active Member (State 2) - Minimalist Refinement
- **Group Identity Card:**
  - Group icon/badge: 54dp (down from 80dp).
  - Group Name: 16sp ExtraBold; Motto: 12sp Regular.
  - Status and ID badges: 10sp SemiBold, padding 6dp x 2dp.
  - 3-Metric Summary row: Font sizes 15sp Bold for numbers, 11sp for labels.
- **Active Battle Card:**
  - Compact padding (12dp).
  - Title: 13sp ExtraBold, time/week remaining: 10sp.
  - Duel scores: 15sp ExtraBold, group names: 12sp Bold.
  - Progress bar: 6dp height with rounded corners.
- **Personal Stats Row (3 Items, Task Count removed):**
  - Columns:
    1. **رتبه من** (Gold trophy icon, 14sp value, 10sp label)
    2. **امتیاز من** (Star icon, 14sp value, 10sp label)
    3. **ساعت مطالعه** (Clock icon, 14sp value, 10sp label)
  - Card height: Compact with 8dp-10dp padding.
- **Members & Medals Table (Task Count removed & RTL Refined):**
  - Tab Row: Compact 38dp height, 13sp font.
  - Table Header Columns (RTL from Right to Left):
    1. `رتبه` (Weight 0.8 / Width ~40dp)
    2. `عضو` (Weight 2.0 - Avatar + Name + "شما" badge)
    3. `ساعت مطالعه` (Weight 1.1)
    4. `امتیاز` (Weight 1.1)
  - Row items:
    - Avatar: 32dp circle.
    - Name: 12.5sp Bold.
    - Study time: 12sp (`X ساعت`).
    - Points: 12sp Bold (`X`).
  - "مشاهده همه" button: Sleek text button with 12sp font.

## 3. Data Flow & Localization
- Ensure all string references use `res/values/strings.xml`.
- Ensure Persian number formatting via `formatPersianNumber()` and `toPersianDigits()`.
