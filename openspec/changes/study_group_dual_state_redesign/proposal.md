# Proposal: Study Group Dual-State Screen Redesign (طراحی دوگانه صفحه گروه‌های من)

## 1. Overview & Context
The **Study Group (گروه‌های من)** module provides a collaborative and competitive learning environment for Iranian students. Depending on whether a user has joined or created a study group, the screen must dynamically render one of two distinct, highly-polished user interfaces matching the provided UI designs:

1. **Empty State (Not Member of Any Group / حالت بدون گروه):**
   - Clean top bar with circular back button and title "گروه‌های من".
   - Search box with placeholder "جستجوی گروه..." and three filter chips: "نام گروه" (Group Name), "# آیدی گروه" (Group ID), "مدال‌ها" (Medals).
   - Central empty-state card featuring a rich 3D illustration of students holding a championship trophy with books, plants, and confetti.
   - Headline: "هنوز عضو هیچ گروهی نیستی!" (You are not a member of any group yet!).
   - Subtitle: "یک گروه بساز یا به گروهی بپیوند و با دوستانت رقابت کن." (Create a group or join one and compete with your friends).
   - Dual Call-to-Action buttons:
     - Primary Filled Button: "ساخت گروه جدید" with plus icon.
     - Outlined Button: "جستجوی گروه‌ها" with search icon.

2. **Active Group State (Member of a Group / حالت عضویت در گروه):**
   - Clean top bar with circular back button and title "گروه‌های من".
   - **Group Identity Card:** Features group badge/banner (mountain peak with flag "قله موفقیت"), group motto ("باهم بهتر، قوی‌تر"), membership status badge ("عضو"), group ID ("ID: 2841"), and 3-metric statistics row (رتبه گروه, امتیاز گروه, تعداد اعضا).
   - **Active Battle Card (نبرد فعال):** Displays ongoing group clash with countdown timer ("۳ روز باقی‌مانده / هفته ۴ از ۱۲"), matchup breakdown ("قله موفقیت" vs "ستارگان دانش"), and interactive dual-colored percentage comparison progress bar.
   - **Personal Performance Row:** Four individual stat cards showing user's rank ("رتبه من"), user's points ("امتیاز من"), study hours ("ساعت مطالعه"), and completed tasks ("تعداد تسک").
   - **Members & Leaderboard Tabbed Section:** Switch between "اعضا" (Members) and "مدال‌ها" (Medals) tabs, with a detailed ranking table highlighting current user ("شما"), showing rank, avatar, name, points, study hours, and task count, plus a "مشاهده همه" expander.

---

## 2. Problem Statement
Currently, the study group screen does not faithfully match the newly provided design mockups for both the empty (non-member) and populated (member) states. Furthermore, the transitions between states, RTL formatting, Persian numeral localization, and visual fidelity need to be brought up to production standards.

---

## 3. Goals & Key Objectives
*   **Exact Visual Fidelity:** Replicate the layout, colors, typography, elevations, badges, and avatars from both UI mockups.
*   **Dynamic State Switching:** ViewModel manages membership status cleanly (`isMember`, `myGroup`, `currentBattle`, `userStats`, `membersList`), allowing seamless previewing or actual API data binding.
*   **Strict RTL & Persian Localization:** Right-to-Left alignment for all badges, tables, progress indicators, and Persian numeral conversions.
*   **Asset & Illustration Generation:** Provide high-resolution vector/image assets for empty-state 3D trophy illustration and group badges.
*   **Test-Driven Reliability:** Provide Robolectric JVM tests verifying ViewModel state transitions, search filters, and UI rendering.

---

## 4. Acceptance Criteria
1. **Empty State UI:**
   - Matches `study-group-with-out-group.png` precisely.
   - Search bar and filter chips ("نام گروه", "# آیدی گروه", "مدال‌ها") respond to taps.
   - Empty card displays 3D trophy illustration, bold title, descriptive subtitle, and two CTA buttons ("ساخت گروه جدید", "جستجوی گروه‌ها").
2. **Active Group State UI:**
   - Matches `study-group-with-group.png` precisely.
   - Group identity card renders banner, title, motto, status badge, group ID, and 3-metric summary.
   - Active battle card displays battle countdown, duel teams, and comparative progress bar.
   - 4-card personal performance metric row displays rank, points, study hours, and task count.
   - Members tab renders ranking table with custom avatars, medals for top 3 ranks, user highlight row ("شما"), and "مشاهده همه" button.
3. **Architecture & Clean Code:**
   - UDF MVVM architecture with Kotlin `StateFlow`.
   - String resources externalized to `res/values/strings.xml`.
   - Semantic test tags on all interactive elements.
   - Automated unit & UI tests passing cleanly.
