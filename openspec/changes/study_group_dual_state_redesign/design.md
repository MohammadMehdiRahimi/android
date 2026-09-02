# Design Document: Study Group Dual-State Screen Redesign

## 1. Architecture & Data Flow

We follow the project's **Feature-First Clean Architecture** with **Unidirectional Data Flow (UDF)** and **MVVM**.

```
                           +------------------------+
                           |     MyGroupScreen      | (Jetpack Compose UI - RTL)
                           +------------------------+
                                     ^    |
                        Observes     |    | Dispatches UI Events
                       StateFlow     |    v
                           +------------------------+
                           |    MyGroupViewModel    |
                           +------------------------+
                                     ^    |
                      Repository Call|    |
                                     v    v
                           +------------------------+
                           |   StudyGroupRepository |
                           +------------------------+
                                     |
                          +----------+----------+
                          |                     |
                          v                     v
                   [ ApiClient / API ]    [ Local Cache / Mock ]
```

---

## 2. State Modeling (`MyGroupUiState`)

```kotlin
data class MyGroupUiState(
    val isLoading: Boolean = false,
    val isMember: Boolean = false, // Governs State 1 (false) vs State 2 (true)
    val errorMessage: String? = null,

    // --- State 1: Non-Member State ---
    val searchQuery: String = "",
    val activeSearchFilter: GroupSearchFilter = GroupSearchFilter.GROUP_NAME,
    val searchResults: List<StudyGroupDto> = emptyList(),

    // --- State 2: Member State ---
    val groupDetails: GroupHeaderData? = null,
    val activeBattle: GroupBattleData? = null,
    val personalStats: PersonalGroupStats? = null,
    val selectedTab: GroupTab = GroupTab.MEMBERS,
    val members: List<GroupMemberUiModel> = emptyList(),
    val badges: List<GroupBadgeDto> = emptyList(),
    val isExpandedMembers: Boolean = false,

    // Dialogs / Sheets
    val showCreateGroupDialog: Boolean = false,
    val showSearchBottomSheet: Boolean = false,
)

enum class GroupSearchFilter {
    GROUP_NAME,
    GROUP_ID,
    MEDALS
}

enum class GroupTab {
    MEMBERS,
    MEDALS
}

data class GroupHeaderData(
    val id: String,
    val numericId: String = "2841",
    val name: String = "قله موفقیت",
    val motto: String = "باهم بهتر، قوی‌تر",
    val roleBadge: String = "عضو",
    val rank: Int = 12,
    val points: Int = 13840,
    val membersCount: Int = 34,
    val avatarUrl: String? = null,
)

data class GroupBattleData(
    val myGroupName: String = "قله موفقیت",
    val myGroupPoints: Int = 13840,
    val opponentGroupName: String = "ستارگان دانش",
    val opponentGroupPoints: Int = 11920,
    val daysRemaining: Int = 3,
    val currentWeek: Int = 4,
    val totalWeeks: Int = 12,
    val myPercentage: Int = 52,
    val opponentPercentage: Int = 48,
)

data class PersonalGroupStats(
    val rank: Int = 3,
    val points: Int = 2450,
    val studyHours: Int = 18,
    val completedTasks: Int = 330,
)

data class GroupMemberUiModel(
    val rank: Int,
    val name: String,
    val isCurrentUser: Boolean = false,
    val points: Int,
    val studyHours: Int,
    val taskCount: Int,
    val avatarRes: Int? = null,
    val avatarColorHex: Long = 0xFF6C5CE7,
)
```

---

## 3. UI Component Specifications

### Common Header
- Top App Bar with circular back button (`Modifier.size(44.dp).clip(CircleShape).background(Color.White).border(1.dp, Color(0xFFE2E8F0))`).
- Centered or right-aligned title: "گروه‌های من" with Persian bold typography (`IranSansFontFamily` / `Vazirmatn`, size 20.sp, `Color(0xFF1E293B)`).

---

### State 1: Non-Member Empty View (`GroupEmptyNonMemberView`)
1. **Search Bar:**
   - Rounded text field (`shape = RoundedCornerShape(24.dp)`) with placeholder "جستجوی گروه...", leading/trailing search icon, clean gray border (`Color(0xFFE2E8F0)`).
2. **Filter Chips Row:**
   - Horizontal row with 3 selectable chips:
     - "نام گروه" with list/clipboard icon.
     - "# آیدی گروه" with hashtag icon.
     - "مدال‌ها" with medal icon.
   - Selected chip: Purple outline (`#6C5CE7`), soft purple background tint (`#F0EDFF`), purple text.
   - Unselected chips: Subtle gray outline, white background, slate text.
3. **Empty Card (`GroupEmptyCard`):**
   - Background: Pure White (`#FFFFFF`), rounded 28.dp corners, elevation/subtle shadow, padding 24.dp.
   - Hero Graphic: 3D trophy with cheering students and books illustration asset (`ic_group_trophy_3d.png` / Vector illustration).
   - Title: "هنوز عضو هیچ گروهی نیستی!" (FontWeight.Bold, 20.sp, `#1E293B`).
   - Subtitle: "یک گروه بساز یا به گروهی بپیوند\nو با دوستانت رقابت کن." (14.sp, `#64748B`, textAlign = Center, lineHeight = 24.sp).
   - Button 1 (Primary): "ساخت گروه جدید" with plus icon in circle, background `#6C5CE7`, rounded 18.dp, height 54.dp.
   - Button 2 (Secondary Outlined): "جستجوی گروه‌ها" with search icon, outline `#6C5CE7`, background `#FFFFFF`, rounded 18.dp, height 54.dp.

---

### State 2: Active Member View (`GroupActiveMemberView`)
1. **Group Overview Card:**
   - Circular mountain illustration with purple flag and summits badge.
   - Title "قله موفقیت", subtitle "باهم بهتر، قوی‌تر".
   - Chip row: Status chip "عضو" (Soft green bg `#E8F8F0`, green text `#10B981`), ID chip "ID: 2841" (Soft gray bg `#F1F5F9`, text `#475569`).
   - Stats divider row: 3 columns with vertical dividers:
     - رتبه گروه 🏆: "۱۲"
     - امتیاز گروه ⭐: "۱۳,۸۴۰"
     - اعضا 👥: "۳۴"
2. **Active Battle Card ("نبرد فعال"):**
   - Header: "نبرد فعال ⚔️" in vibrant purple (`#6C5CE7`).
   - Meta info: "۳ روز باقی‌مانده 🕒 | هفته ۴ از ۱۲".
   - Teams clash: "قله موفقیت (۱۳,۸۴۰)" × "ستارگان دانش (۱۱,۹۲۰)".
   - Comparative dual-color bar: `myPercentage` (52% solid purple) vs `opponentPercentage` (48% soft lavender), with percentage labels on either side.
3. **Personal Stats 4-Card Grid:**
   - 4 rounded cards horizontally spaced:
     - Card 1: "رتبه من 👑" -> "۳" (Warm golden accent `#FEF3C7`, `#B45309`)
     - Card 2: "امتیاز من ⭐" -> "۲,۴۵۰" (Soft blue/cyan accent `#E0F2FE`, `#0369A1`)
     - Card 3: "ساعت مطالعه 🕒" -> "۱۸" (Soft gray `#F8FAFC`, `#334155`)
     - Card 4: "تعداد تسک 📊" -> "۳۳۰" (Soft purple `#F3E8FF`, `#7E22CE`)
4. **Leaderboard & Badges Tabbed Card:**
   - Segmented Tab Header: "اعضا 👥" (Selected with indicator) / "مدال‌ها 🏅".
   - Table Columns Header: `رتبه` | `عضو` | `امتیاز` | `ساعت مطالعه` | `تعداد تسک`
   - Member Rows:
     - Rank 1: 🥇 الهام (Avatar) | ۳,۱۲۰ | ۲۳ ساعت | ۴۸۰ تسک
     - Rank 2: 🥈 سارا (Avatar) | ۲,۸۱۰ | ۲۰ ساعت | ۴۱۰ تسک
     - Rank 3 (Current User): 🥉 امیر (شما badge) [Highlighted with soft purple tint `#F5F3FF`] | ۲,۴۵۰ | ۱۸ ساعت | ۳۳۰ تسک
     - Rank 4: ۴ آرین (Avatar) | ۱,۸۹۰ | ۱۵ ساعت | ۲۸۰ تسک
     - Rank 5: ۵ محمد (Avatar) | ۱,۵۷۰ | ۱۲ ساعت | ۲۱۰ تسک
   - Bottom CTA: "مشاهده همه ❮" clickable text to expand or view full roster.

---

## 4. Assets & Visual Polish
- Generate/render high quality 3D trophy students illustration for State 1.
- Generate/render mountain summit badge with purple flag for State 2.
- Vector icons for badges, medals, swords, trophies, and avatars.
