# Proposal: Create Study Plan Pixel-Perfect Redesign (صفحه ایجاد برنامه مطالعاتی)

## 1. Overview & Context
When a user clicks the `+` button in the study tasks screen (`StudyPlanScreen`), they navigate to the Create Study Plan screen. This proposal details the complete pixel-perfect redesign of `CreateStudyPlanScreen` to match the user's reference design screenshot with high-fidelity Android Material 3 styling, strictly localized in Persian (RTL).

Per user request:
- **Header Avatar Exclusion:** The user explicitly instructed: *"فقط هدرش که تصویر کاربر هست و نمیخوام بزنی. بقیه شو بزن."* (Do not include the user avatar image in the header; include all other elements).
- **Pixel-to-Pixel Fidelity:** Strictly replicate colors, typography, cards, radii, spacing, shadows, and interactive states from the reference design.
- **Mobile-First Android:** Optimized for standard smartphone screen widths (e.g. Samsung/Pixel devices) without desktop-like stretching.

---

## 2. Acceptance Criteria

### 2.1 Header (هدر)
- Circular back button on the top-start with navigation pop action.
- Title: «برنامه‌ریزی برای علی محمدی» (or dynamic user name from `TokenManager`/profile).
- Subtitle: «برنامه درسی و مطالعه» in subtle gray.
- User avatar image omitted as explicitly instructed.

### 2.2 Date Selector (انتخاب تاریخ)
- Compact horizontal calendar card displaying week days (شنبه تا جمعه):
  - Each item displays day of week (e.g., دوشنبه), day of month (e.g., ۱۴), and month name (e.g., اردیبهشت).
  - Selected day is styled with an active purple gradient card (`#6C47FF` to `#5438DC`), crisp white typography, and a distinct dot indicator under the date.
  - Non-selected days have clean neutral styling.
- Navigation arrows for previous (`<`) and next (`>`) days/weeks.
- Small purple calendar icon on the start side.

### 2.3 Daily Summary Card (خلاصه روز)
- Elevated white card with 3 key metrics arranged in balanced columns:
  1. **کل زمان مطالعه:** Total study duration (e.g., «۶:۳۰ ساعت») with purple circular clock icon.
  2. **تعداد جلسات:** Total study sessions count (e.g., «۵ جلسه») with purple list/bullet icon.
  3. **پیشرفت روز:** Daily completion rate (e.g., «۳۰٪») with a sleek circular progress indicator.

### 2.4 Study Sessions List (لیست جلسات برنامه)
- Interactive list of scheduled study sessions. Each item card features:
  - **Checkbox:** Rounded purple checkbox on the start side (filled with checkmark when completed, empty outline when pending).
  - **Subject Icon & Palette:** Rounded square container with pastel background and dedicated subject icon:
    - زیست‌شناسی: Green leaf icon on light green background (`#E8F5E9`).
    - ریاضی: Math radical/formula icon (`√x`) on soft orange background (`#FFF3E0`).
    - شیمی: Chemistry flask icon on soft red/pink background (`#FFEBEE`).
    - فیزیک: Physics atom icon on soft blue background (`#E3F2FD`).
    - ادبیات فارسی: Book icon on soft purple background (`#EDE7F6`).
    - مرور و تست: Checklist/document icon on soft cyan background (`#E1F5FE`).
  - **Subject Information:** Subject title (bold) and chapter/topic description (e.g., «فصل ۱ | گفتار ۱ و ۲»).
  - **Time & Duration:** Start time (e.g., «۰۸:۰۰», «۱۰:۰۰», «۱۳:۳۰») and duration badge (e.g., «۹۰ دقیقه»).
  - **Session Status Badges:**
    - Completed: Soft green badge «انجام شده» (`#E8F5E9` bg, `#10B981` text).
    - Next session: Soft purple badge «بعدی» (`#EDE7F6` bg, `#7C4DFF` text).
    - Highlighted border: The "Next" session card features a subtle purple outline (`1.5.dp`, `#7C4DFF`).
  - **Session Actions:** Three-dots overflow menu (`Icons.Default.MoreVert`) for editing or deleting a session.

### 2.5 List Footer Action (افزودن جلسه)
- Full-width outlined button with purple border and text: «+ افزودن جلسه».
- Tapping opens an interactive dialog/bottom sheet to add a new study session or choose from study catalog.

### 2.6 Bottom Sticky Bar (اکشن‌های پایانی)
- Fixed bottom container adhering to navigation bar insets:
  - **Primary Action (Purple Button):** «ذخیره برنامه روز ✓» (`#6C47FF` background, white text, bold).
  - **Secondary Action (Outlined Button):** «کپی از روز قبل» with copy icon.

---

## 3. Non-Functional Requirements
- **RTL & Persian:** 100% native RTL layout direction, IranSans/Vazirmatn font families, Persian numerals (`toPersianNumber()`).
- **Clean Architecture & UDF:** Single immutable UI state, MVVM with `CreateStudyPlanViewModel`, clear event dispatching.
- **Responsiveness:** Fluid width with mobile-first constraints (`widthIn(max = 560.dp)`), smooth scrolling, zero horizontal overflow.
- **Testing:** Comprehensive unit tests and Robolectric UI tests.
