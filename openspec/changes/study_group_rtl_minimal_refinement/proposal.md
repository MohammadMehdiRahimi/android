# Proposal: Study Groups Screen RTL Refinement, Minimalist Styling & Typography Adjustment

## 1. Context & Motivation
The user requested specific visual and layout improvements for the Study Groups ("گروه‌های من") feature:
1. Ensure full Right-to-Left (RTL) alignment and orientation across all components, input fields, badges, and layout rows.
2. Reduce the font size of titles/headers and badges for a tighter, cleaner, and more balanced visual hierarchy.
3. In the member list / leaderboard table:
   - Ensure strictly RTL column order (رتبه, عضو, ساعت مطالعه, امتیاز).
   - Completely remove the "تعداد تسک" (task count) column and metric.
4. Apply a more **minimalist, compact, and polished design** across all cards, chips, buttons, and summary grids (reducing excessive padding, using tighter card radii, refined badges, and clean spacing).

## 2. Scope & Changes
- **RTL & Input Fields:**
  - Enforce `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl)` consistently.
  - Search input field text alignment set to start/RTL.
  - Top bar back button and title arranged strictly RTL (title centered or start-aligned properly, back button using AutoMirrored icon and positioned appropriately).
- **Typography & Font Sizes:**
  - Reduce oversized typography:
    - Screen top bar title: 20sp -> 17sp / 18sp.
    - Card titles & group names: 20sp/21sp -> 16sp / 17sp.
    - Metric headline values: 20sp -> 16sp / 17sp.
    - Subtitles & descriptions: 13sp/14sp -> 12sp.
    - Chips & badges: 11sp/12sp -> 10sp / 11sp.
- **Member List & Table Refactoring:**
  - Remove "تعداد تسک" (Task count) from:
    - The personal summary cards (switch from 4 cards to 3 cleaner cards: رتبه من, امتیاز من, ساعت مطالعه, or cleaner 3-item row).
    - The members leaderboard table header and rows (remove the task count column entirely).
  - Table columns in RTL order:
    - `رتبه` (Rank badge / number)
    - `عضو` (Avatar + Name + "شما" badge if current user)
    - `ساعت مطالعه` (Study time)
    - `امتیاز` (Points)
- **Minimalist Aesthetic:**
  - Reduce bulky paddings (e.g., from 32dp/24dp down to 14dp/16dp).
  - Streamline card shadows and borders for a modern, lightweight, flat-yet-elevated appearance.
  - Make filter chips, search bar, and CTA buttons sleek and compact.

## 3. Acceptance Criteria
- [x] Complete RTL orientation and right-aligned text fields in both Non-Member (Empty) and Active Member views.
- [x] Font sizes for titles and headers are visibly smaller, sleek, and harmonious.
- [x] "تعداد تسک" is removed from the personal stats and the member leaderboard table.
- [x] The member table is strictly RTL with 4 columns (رتبه, عضو, ساعت مطالعه, امتیاز) and clean layout.
- [x] Visual design is compact, minimalist, and lightweight with proper spacing.
- [x] All unit and Robolectric tests pass with zero errors.
