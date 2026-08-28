# Feature Proposal: Academic Analyzer Screen (صفحه تحلیلگر)

## 1. Description & Context
Transform the Academic Report / Analyzer screen (`AcademicReportScreen.kt` / `AnalyzerScreen`) to precisely match the provided high-fidelity visual design for the Iranian educational platform Shetab:
- **Top Header**: Profile avatar with active status indicator, title "تحلیلگر" with subtitle "گزارش کامل پیشرفت شما", and notification icon badge.
- **Timeframe Selector**: Interactive timeframe tabs ("هفته گذشته", "ماه گذشته", "۳ ماه گذشته") with custom dropdown and calendar icon styling.
- **AI Smart Analysis Section ("تحلیل هوشمند Ai")**:
  - Cute 3D AI Robot graphic with sparkle speech bubble.
  - Performance insights and actionable recommendations in Persian.
  - 3 quick stat chips: "پیشنهاد ما" (Target/Bullseye), "بیشترین تمرکز" (Crescent Moon), "سبک یادگیری" (Eye/Visual).
- **Summary Metrics Cards**:
  - 4 key metric cards: "تعداد آزمون" (Exam count), "تست غلط" (Wrong tests with downward trend), "تست صحیح" (Correct tests with success rate), "تعداد تست" (Total tests with upward trend).
- **Strengths & Weaknesses ("نقاط قوت" و "نقاط ضعف")**:
  - Two side-by-side comparison cards with custom segmented/rounded progress bars for each subject percentage and "مشاهده جزئیات" actions.
- **Study Time Distribution Chart ("توزیع زمان مطالعه در طول روز")**:
  - Custom Canvas smooth spline/line chart showing hours studied across time buckets (۰-۴, ۴-۸, ۸-۱۲, ۱۲-۱۶, ۱۶-۲۰, ۲۰-۲۴) with active peak badge ("۳ ساعت").
- **Periodic Reports Banner ("گزارش‌های دوره‌ای")**:
  - Interactive banner with report download ("دریافت گزارش") and peer comparison ("مقایسه با دوستان") buttons alongside a decorative clipboard illustration.

## 2. Acceptance Criteria
- Pixel-accurate layout and typography matching the uploaded design reference.
- Strict Persian RTL support, Iranian font family, and Persian numerals (`toPersianNumber()`).
- Responsive, clean Material 3 design with zero jank, lazy loading via `LazyColumn`, and decoupled ViewModel.
