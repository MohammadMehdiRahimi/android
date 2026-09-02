# Proposal: Add Study Session Screen Redesign (طراحی مجدد صفحه افزودن جلسه)

## 1. Context & Motivation
In the Shetab (شتاب) study planning module, users manage their daily and weekly learning routines. When tapping the "افزودن جلسه" (Add Session) button on the weekly study planner (`StudyPlanScreen`), users should be presented with a modern, focused, pixel-accurate "Add Study Session" interface matching the provided reference design.

The previous screen implementation (`CreateStudyPlanScreen`) contained legacy multi-step wizard logic, complex overlays, and divergent styling. This proposal specifies a complete replacement and redesign with a clean, pixel-perfect, single-card layout that precisely matches the reference screenshot, honoring Iranian Persian typography (Vazirmatn/IranSans), full RTL layout direction, Persian numerals, responsive sizing, and clean architecture integration.

---

## 2. Scope & Key Changes
*   **Legacy Code Removal:** Deprecate and replace the old complex wizard UI in `CreateStudyPlanScreen.kt` with the dedicated, pixel-accurate "Add Study Session" layout.
*   **Header Section:**
    *   Top app bar with circular soft-bordered action buttons: back button on the left (`Icons.AutoMirrored.Filled.ArrowBack`), open book icon on the right (`Icons.Outlined.MenuBook`).
    *   Centered titles: "افزودن جلسه" (Add Session) and Persian subtitle "جزئیات جلسه درس و مطالعه را وارد کنید".
*   **Main Container Card:**
    *   A large rounded rectangular white card (`shape = RoundedCornerShape(28.dp)`), very subtle elevation and light purple-tinted border (`#EFEBFB`).
*   **Course & Grade Selection:**
    *   Header row featuring the section title "درس و کتاب ۱" with purple book icon on the right.
    *   Dropdown chip on the left: "پایه دوازدهم" with chevron arrow, dynamically opening a grade selector.
    *   Subject selector chips row: "اقتصاد", "تاریخ", "جامعه شناسی", "جغرافیا" with active purple border, check badge, and selected state.
*   **Chapter & Topic Section:**
    *   Nested rounded card with lavender/gray background (`#F8F7FD`).
    *   Section title "فصل و مباحث ۱" with purple clipboard icon.
    *   Dropdown selector for chapters: "۱: کسب‌وکار و کارآفرینی" with arrow indicator.
    *   Selectable topic radio items: "موفقیت و شکست کسب‌وکارها" (checked) and "کارآفرینی و نقش" (unchecked).
*   **Add Chapter Button:**
    *   Full-width light lavender button "+  اضافه کردن فصل" allowing dynamic addition/expansion of study chapters.
*   **Study Cycles & Schedule Section:**
    *   Horizontal divider line.
    *   Section header "زمان‌بندی و دوره‌های مطالعه" with clock icon.
    *   Interactive counter pill: decrement button (`-`), count label with Persian numerals ("۳ دوره"), increment button (`+`).
*   **Study & Rest Sliders:**
    *   Two side-by-side controls with icons, titles, and selected values:
        *   **Rest Time ("زمان استراحت"):** Coffee cup icon, value "۱۵ د", slider with ticks (۵, ۱۰, ۱۵, ۲۰, ۳۰).
        *   **Study Time ("زمان مطالعه"):** Book icon, value "۴۵ د", slider with ticks (۱۵, ۳۰, ۴۵, ۶۰, ۹۰).
    *   Functional custom interactive sliders with purple active track, circular thumb, tick marks, and Persian numeral labels.
*   **Information Notice Banner:**
    *   Lavender info card with purple information circle icon and text: "با ثبت این جلسه، برنامه درسی شما به‌روزرسانی خواهد شد."
*   **Bottom Actions:**
    *   Side-by-side buttons:
        *   Primary action "ثبت جلسه" (Submit Session) with checkmark icon and purple gradient background.
        *   Secondary action "انصراف" (Cancel) with duplicate/rectangles icon, white background, and purple outline.

---

## 3. Acceptance Criteria
1.  **Pixel-Accurate Visual Fidelity:** Visual design, margins, paddings, typography sizes, colors, icons, and border radii closely mirror the reference image.
2.  **RTL Compliance:** Native Right-to-Left layout throughout the screen using `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl)`. No physical `left`/`right` modifiers used.
3.  **Persian Typography & Numerals:** Persian numerals (e.g. ۳ دوره, ۱۵ د, ۴۵ د, ۵, ۱۰, ۱۵, ۲۰, ۳۰, ۹۰) formatted correctly.
4.  **Interactive Elements:**
    *   Subject chips are clickable and update selected subject state.
    *   Topic radio buttons toggle selection.
    *   Chapter dropdown and Grade dropdown are interactive.
    *   Counter increments and decrements study cycles.
    *   Sliders update rest and study duration values smoothly.
    *   "ثبت جلسه" invokes task creation/submission and navigates back or shows success feedback.
    *   "انصراف" dismisses or navigates back safely.
5.  **Integration & Navigation:** Accessible directly from `StudyPlanScreen` via the add task floating action button (`navController.navigate("create_study_plan")`) and back navigation works seamlessly.
6.  **Automated Testing:** Robolectric tests verifying state transitions, user input handling, and RTL layout rendering.
