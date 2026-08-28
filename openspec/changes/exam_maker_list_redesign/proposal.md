# Proposal: Exam Maker List Screen Redesign (صفحه لیست آزمون‌های من)

## 1. Executive Summary & Intent
Redesign the initial screen of the **Exam Maker (آزمون‌ساز)** module ("آزمون‌های من" / My Exams) to achieve pixel-perfect alignment with the provided UI mockup. The screen displays the user's exam history with rich categorization, filters, summary counters, and clean card items with performance statistics (score, duration, question count).

## 2. Problem Statement
The current exam builder flow lacks the polished visual hierarchy, Iranian educational context, dynamic multi-facet filtering (date, subject, topic), card metadata styling (subject-themed icon badges, test vs. descriptive tags, scores with chart iconography), and quick floating creation CTA demonstrated in the new UI mockup.

## 3. Scope of Changes
1. **Top Header**:
   - Right-to-Left (RTL) header displaying "آزمون‌های من" with a clipboard icon and a notification bell icon on the opposite side.
2. **Filter Chips Bar**:
   - 3 interactive dropdown filter buttons:
     - "همه تاریخ‌ها" (All Dates) with calendar icon.
     - "همه درس‌ها" (All Subjects) with book icon.
     - "همه مباحث" (All Topics) with stacked layers icon.
3. **Summary Header**:
   - Total exams count label: "تعداد کل: ۱۲ آزمون" with bold purple accent on the count.
4. **Exam Card Items (LazyColumn)**:
   - Subject-specific color badges with checklist icon & subtle chevron indicator.
   - Test Type Tag: "تستی" (Purple pill) and "تشریحی" (Green pill).
   - Subject & Topic hierarchy (e.g., "ریاضی دهم" - "معادله و نامعادله").
   - Persian Date & Weekday display (e.g., "۱۴۰۳/۰۳/۲۵" - "جمعه").
   - 3-column bottom statistics:
     - **Score ("نمره")**: Highlighted purple value (e.g., "۱۸/۳۰") with chart icon.
     - **Time ("زمان")**: Formatted duration (e.g., "۴۵ دقیقه") with clock icon.
     - **Question Count ("تعداد تست")**: Formatted count (e.g., "۳۰") with quiz list icon.
5. **Floating Action Button (FAB)**:
   - Primary purple gradient floating action button with `+` icon positioned at the bottom start for building a new custom exam.
6. **Navigation & Interaction**:
   - Clicking a card navigates to the exam details/review.
   - Clicking the FAB navigates to the custom exam creator wizard.

## 4. Acceptance Criteria
- [ ] Header renders title "آزمون‌های من" with clipboard icon and notification button in native RTL.
- [ ] Filter bar displays all three dropdown selectors ("همه تاریخ‌ها", "همه درس‌ها", "همه مباحث") with corresponding icons.
- [ ] Total count section accurately reflects filtered/total exams count in Persian typography.
- [ ] Exam cards match the UI prototype in spacing, typography, colors, tags ("تستی"/"تشریحی"), subject-themed icon badges, and 3-column stats with dividers.
- [ ] FAB button is floating at the bottom with standard touch target and launches the exam builder.
- [ ] ViewModel manages filter states, search/filter queries, and exam item data via UDF (`StateFlow`).
- [ ] Unit & Robolectric tests verify ViewModel state changes and UI rendering.
