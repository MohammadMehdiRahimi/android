# Proposal: Exam Maker List Screen Redesign (صفحه لیست آزمون‌ها در آزمون‌ساز)

## 1. Executive Summary & Intent
Redesign the initial screen of the **Exam Maker (آزمون‌ساز)** module ("آزمون‌های من" / My Exams) to strictly match the provided UI design (`exam-list-page.png`). The updated screen presents the user's exam history with elegant typography, multi-facet filtering (date, subject, topic), total exam count indicator, rich card metrics (score, duration, question count), exam type badges ("تستی" / "تشریحی"), subject-themed icon badges, and a prominent Floating Action Button (FAB) to create new exams.

## 2. Problem Statement
The current exam maker screen doesn't align with the sleek, high-contrast, card-based layout shown in the provided visual design. Key missing elements include:
- The top header with "آزمون‌های من" + clipboard icon and notification bell.
- The 3-chip filter bar ("همه تاریخ‌ها", "همه درس‌ها", "همه مباحث") with chevrons and icons.
- The summary counter ("تعداد کل: ۱۲ آزمون") with emphasized accent digits.
- The refined card layout with Persian date/day-of-week on the left, subject/topic and exam type badge in the center/right, subject-colored checklist icon, right chevron, and the 3-column stats section at the bottom (score, duration, test count).
- The bottom-left circular floating action button with `+` icon for building new exams.

## 3. Scope of Changes
1. **Top Header**:
   - Native RTL top bar with title "آزمون‌های من" beside an exam checklist icon, and a notification bell icon on the opposing side.
2. **Filter Section**:
   - 3 rounded filter dropdown chips:
     - "همه تاریخ‌ها" (All Dates) with calendar icon and dropdown chevron.
     - "همه درس‌ها" (All Subjects) with book icon and dropdown chevron.
     - "همه مباحث" (All Topics) with layers/topics icon and dropdown chevron.
   - Interactive bottom sheets or dropdown selectors for active filtering.
3. **Summary Badge**:
   - "تعداد کل: ۱۲ آزمون" with "تعداد کل:" in subtle secondary text and Persian number + "آزمون" in bold primary purple accent.
4. **Exam Card Item Design**:
   - Top Section:
     - Persian Date & Day of the week (e.g., "۱۴۰۳/۰۳/۲۵", "جمعه").
     - Subject Title (e.g., "ریاضی دهم", "فیزیک دهم", "شیمی دهم", "زیست دهم").
     - Topic Subtitle (e.g., "معادله و نامعادله", "فشار و آثار آن", "ساختار اتم", "گوارش و جذب مواد").
     - Test Type Pill Tag ("تستی" with soft purple background & purple text, "تشریحی" with soft green background & green text).
     - Subject Avatar Icon: Colored rounded square (Purple for Math, Light Green for Physics, Soft Orange for Chemistry, Soft Blue for Biology) with checklist vector icon.
     - Directional navigation indicator / subtle chevron.
   - Bottom Section:
     - 3-column stats separated by subtle vertical dividers:
       - **نمره (Score)**: e.g. "۱۸/۳۰" with bar chart icon in primary accent.
       - **زمان (Duration)**: e.g. "۴۵ دقیقه" with schedule/clock icon.
       - **تعداد تست (Question Count)**: e.g. "۳۰" with list/toc icon.
5. **Floating Action Button (FAB)**:
   - Floating circular purple button with `+` icon at the bottom start (left in RTL layout) triggering the custom exam builder flow (`build_exam`).
6. **Architecture & State Management**:
   - MVVM with `ExamsViewModel` exposing `ExamsUiState` via `StateFlow`.
   - Dynamic filtering of exam history by Subject, Date, and Topic.

## 4. Acceptance Criteria
- [ ] Top bar displays "آزمون‌های من" with checklist icon and notification button in native RTL.
- [ ] Filter bar contains three interactive dropdown chips ("همه تاریخ‌ها", "همه درس‌ها", "همه مباحث").
- [ ] Total count displays accurately formatted Persian numbers and total exams matching the filtered state.
- [ ] Exam cards match the UI prototype in layout, padding, font weights, colors, badges, and 3-column bottom metrics.
- [ ] FAB button is floating at the bottom start with touch target >= 48dp and opens the custom exam creation wizard.
- [ ] ViewModel and UI tests pass with high coverage under Robolectric.
