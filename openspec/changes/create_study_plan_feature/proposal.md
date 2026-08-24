# Refined Feature Proposal: Study Plan Screen & Create Study Plan Flow

## 1. Context & User Feedback
The user provided detailed visual and functional feedback based on the live app and UI mockup:

### Issue 1: Floating Action Button (+) Position
- In RTL layout direction, `Alignment.BottomStart` resolved to the bottom-right of the screen.
- **Requirement:** The '+' FAB must be strictly on the **bottom-left** of the screen (`Alignment.BottomEnd` in RTL or `AbsoluteAlignment.BottomLeft`).

### Issue 2: Filter Chips on Study Plan Screen
- The filter row currently clips or wraps text ("همه", "در حال", "انجام", "انجام").
- **Requirement:** Beautiful pill chips matching the original design ("همه", "در حال انجام", "انجام نشده", "انجام شده" / "پایان یافته") with ample horizontal padding, correct icon indicators, and smooth horizontal scrolling or clean auto-sizing.

### Issue 3: Create Study Plan Customizations
- **3.1 User Field/Major Dynamic Binding:** The field displayed on book cards and used to filter books must match the user's registered major (e.g. from `TokenManager.getUser()`: "رشته تجربی", "رشته ریاضی و فیزیک", "رشته علوم انسانی").
- **3.2 & 3.3 Topics Section:**
  - Topics must have wider chips with horizontal scroll (`LazyRow`).
  - Chapter selector/dropdown so the user can easily switch chapters.
  - Selected topics must clearly show the checkmark next to them.
- **3.4 Study Periods Section:**
  - Distinct section title "دوره‌های مطالعه" with sync icon on top/header.
  - Clean counter with minus/plus and smaller descriptive text for "هر دوره شامل مطالعه و استراحت".
- **3.5 Sliders & Triggers:**
  - Compact, neat stepped sliders with crisp tick points and exact typography matching `create-task-ui.png`.
- **3.6 Remove Summary Section:**
  - Remove "خلاصه برنامه شما" matrix section completely from the Create Study Plan screen.
