# Design Specification: Exams List Skeleton, RTL Directional Arrows, and Step 3 Screen Redesign

## 1. Architectural Strategy & Modular Design

All modifications are confined to the Presentation layer under `com.example.ui.features.exams`:
- `ExamsScreen.kt`: Handles the main exams list, granular skeleton states, and filter chips.
- `ExamItemCard.kt`: Handles exam card presentation and navigation arrow icon.
- `Step3ExamSummaryScreen.kt` (or within `BuildExamScreen.kt`): Dedicated, modular Composable implementing the pixel-perfect layout of `exam-create-3.png`.

---

## 2. Component Specifications

### 2.1 Granular Skeleton Loading in `ExamsScreen.kt`
- **Static Content Always Rendered**:
  - Top search bar & filter chips row.
  - "آزمون‌های من" title and "تعداد کل:" text label.
- **Dynamic Content in Loading State**:
  - Count value: Display a shimmering rounded box (`width = 24.dp`, `height = 16.dp`).
  - Content list: Instead of a generic full-screen spinner or full blank skeleton, render a `LazyColumn` containing 3 `ExamItemCardSkeleton` cards with shimmering title, tag chips, question count, and date placeholders.

### 2.2 Exam Card Directional Navigation Arrow
- Update `ExamItemCard.kt` to replace right chevron (`Icons.Filled.KeyboardArrowRight`) with left chevron (`Icons.AutoMirrored.Filled.KeyboardArrowLeft` or `Icons.Filled.KeyboardArrowLeft`) with a subtle circular background or icon tint.

### 2.3 Step 3 Exam Creation Summary Screen (`exam-create-3.png`)
The screen will be structured into a vertically scrolling `Column` with 16.dp horizontal padding:

#### A. Stepper Header
- Top step indicator displaying 3 steps connected by dashed/dotted lines:
  - Step 1: `۱. ساختار آزمون` (Completed checkmark circle `Color(0xFFF3E8FF)` with `Color(0xFF7C3AED)` icon).
  - Step 2: `۲. تنظیم سوالات` (Completed checkmark circle).
  - Step 3: `۳. خلاصه و ساخت آزمون` (Active purple filled circle `Color(0xFF6366F1)` with white text `۳`).

#### B. General Summary Section (خلاصه کلی آزمون)
- White card container (`RoundedCornerShape(16.dp)`):
  - Section Header: `Icons.Outlined.Assignment` + "خلاصه کلی آزمون" (Right-aligned in RTL).
  - 2-row x 4-column responsive grid (or 2 rows of 4 cards) containing:
    1. **نوع آزمون**: `آزمون تستی` (Icon: list bullet)
    2. **پایه**: `دهم` (Icon: school / graduation cap)
    3. **رشته**: `ریاضی فیزیک` (Icon: science flask)
    4. **تعداد کتاب‌ها**: `۲ کتاب` (Icon: book)
    5. **منبع سوال**: `تألیفی / کنکور` (Icon: bank / institution)
    6. **نمره منفی**: `فعال` (Icon: remove / minus circle)
    7. **چینش سوالات**: `تصادفی` (Icon: shuffle)
    8. **مدت زمان تقریبی**: `۴۵ دقیقه` (Icon: schedule / clock)

#### C. Question Statistics Section (آمار سوالات آزمون)
- White card container (`RoundedCornerShape(16.dp)`):
  - Section Header: `Icons.Outlined.BarChart` + "آمار سوالات آزمون".
  - Metrics Row:
    - `مجموع سوالات`: `۲۸ سوال` (Bold purple `Color(0xFF4F46E5)`).
    - Vertical divider.
    - `آسان`: `۸` (Green dot `Color(0xFF10B981)`).
    - `متوسط`: `۹` (Yellow dot `Color(0xFFF59E0B)`).
    - `دشوار`: `۶` (Orange dot `Color(0xFFF97316)`).
    - `خیلی دشوار`: `۵` (Red dot `Color(0xFFEF4444)`).
  - Multi-colored horizontal segmented progress bar matching the exact ratios of easy/medium/hard/very hard questions with rounded pill ends.

#### D. Exam Sections Breakdown (بخش‌های آزمون)
- White card container (`RoundedCornerShape(16.dp)`):
  - Section Header: `Icons.Outlined.MenuBook` + "بخش‌های آزمون".
  - Book Detail Sub-cards (e.g. ریاضی دهم, ریاضی یازدهم):
    - Rounded border card with light background.
    - Right side: Gradient book cover artwork with title.
    - Center column: Book title, Chapter name ("فصل ۲: تابع"), Selected topic pill badges ("تابع و نمودار", "دامنه و برد", "ترکیب توابع").
    - Left side: Pill badge with "تعداد کل" / "۱۴ سوال".
    - Bottom row inside card: Difficulty breakdown with color dots (آسان ۴, متوسط ۵, دشوار ۳, خیلی دشوار ۲).

#### E. Exam Tips Section (نکات آزمون)
- White card container (`RoundedCornerShape(16.dp)`):
  - Header: `Icons.Outlined.Info` + "نکات آزمون".
  - 3 rows with dashed guideline dividers:
    1. `بعد از ساخت، آزمون در لیست آزمون‌های شما قرار می‌گیرد.` (Icon: list in circle)
    2. `می‌توانید بعداً آزمون را ویرایش یا دوباره استفاده کنید.` (Icon: edit in circle)
    3. `پاسخ‌نامه و گزارش بعد از اتمام آزمون فعال می‌شود.` (Icon: analytics in circle)

#### F. Bottom Navigation & Action
- Primary Button: "ساخت آزمون" with spark icon (`Icons.Filled.AutoAwesome`), height 50.dp, vibrant purple background (`Color(0xFF6366F1)`).
- Secondary Text Button: "مرحله قبل" (`Color(0xFF6366F1)`).
- Bottom navigation insets (`Modifier.navigationBarsPadding()` + 24.dp spacer) for clear ergonomics above Android navigation bars.

---

## 3. Testing Plan
- Unit tests & Robolectric tests to verify:
  1. Granular skeleton loading behavior in `ExamsScreenTest`.
  2. RTL forward navigation icon in `ExamItemCard`.
  3. Step 3 component rendering, calculation of total questions, and difficulty distribution progress bar rendering.
