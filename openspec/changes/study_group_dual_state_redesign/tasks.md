# Tasks: Study Group Dual-State Screen Redesign

- [x] **1. Data Layer & Models (`com.example.ui.features.mygroup`)**
  - [x] 1.1 Define strongly-typed UI models for dual states (`MyGroupUiState`, `GroupHeaderData`, `GroupBattleData`, `PersonalGroupStats`, `GroupMemberUiModel`, `GroupSearchFilter`, `GroupTab`).
  - [x] 1.2 Update `MyGroupViewModel` with clean state management, search query filtering, tab selection, member expansion, and toggle/mock helper for previewing both states.

- [x] **2. Visual Assets & Illustrations**
  - [x] 2.1 Set up high-quality 3D study group trophy illustration for State 1 empty card.
  - [x] 2.2 Set up mountain summit badge for State 2 group identity card.
  - [x] 2.3 Ensure member avatar drawables/placeholders are properly provided.

- [x] **3. UI Components & Presentation Layer (`com.example.ui.features.mygroup`)**
  - [x] 3.1 Implement Common Top Bar with circular back button and "گروه‌های من" title.
  - [x] 3.2 Implement State 1 (Non-Member):
    - [x] Search text field with rounded borders and search icon.
    - [x] Filter chips row ("نام گروه", "# آیدی گروه", "مدال‌ها") with active selection styling.
    - [x] Empty state card with 3D illustration, title, subtitle, and primary/secondary CTA buttons.
  - [x] 3.3 Implement State 2 (Active Member):
    - [x] Group Overview Card with mountain badge, name, motto, status chips, and 3-stat divider metrics.
    - [x] Active Battle Card ("نبرد فعال") with battle countdown, duel teams, and comparative percentage bar.
    - [x] 4-item Personal Performance Stat cards (رتبه من, امتیاز من, ساعت مطالعه, تعداد تسک).
    - [x] Members & Medals tabbed table with custom avatars, medals for top 3, current user ("شما") row highlight, and "مشاهده همه" expansion.
  - [x] 3.4 Integrate state switching seamlessly in `MyGroupScreen.kt`.

- [x] **4. Strings & Localization**
  - [x] 4.1 Add all Persian strings to `res/values/strings.xml`.
  - [x] 4.2 Ensure Persian numeral formatting across all stats, countdowns, ranks, and percentages.

- [x] **5. Testing & Verification**
  - [x] 5.1 Create Robolectric unit tests `MyGroupDualStateTest.kt` verifying both non-member and member state transitions, calculations, search filtering, and tab switching.
  - [x] 5.2 Compile applet and verify zero build errors.
