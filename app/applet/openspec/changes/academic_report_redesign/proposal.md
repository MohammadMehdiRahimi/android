# Proposal: Academic Report Screen Complete Redesign (صفحه تحلیل)

## 1. Description
Replace the previous academic report / analysis screen with a 1:1 pixel-perfect implementation of the provided UI design (`صفحه تحلیل.png`), incorporating the custom `ai-vector.png` asset provided in the project root.

## 2. Acceptance Criteria (Matching Provided UI)
*   **Header Bar:**
    *   Left (RTL end): Notification bell icon with purple indicator dot in a rounded square container.
    *   Center: Title "تحلیل" with subtitle "گزارش کامل پیشرفت شما".
    *   Right (RTL start): User avatar image with green online status dot indicator.
*   **Time Period Selector (Segmented Filters):**
    *   3 Filter options: "هفته گذشته" (default selected with purple background `#5B42F3`, calendar icon, and dropdown arrow), "۳ ماه گذشته", and "ماه گذشته".
*   **AI Smart Analysis Hero Card (`تحلیل هوشمند`):**
    *   Purple "Ai" badge header with title "تحلیل هوشمند".
    *   Natural language AI insight summary text.
    *   3D AI Robot illustration on the left using the newly integrated `ai-vector.png` asset with sparkle speech bubble.
    *   3 bottom feature insight cards:
        1. **پیشنهاد ما:** "مرور مباحث ادبیات" (Target / bullseye icon in red)
        2. **بهترین تمرکز:** "شب‌ها (۲۰-۲۵)" (Crescent moon icon in purple)
        3. **سبک یادگیری:** "دیداری" (Eye icon in purple)
*   **2x2 Performance Metrics Grid:**
    1. **تست صحیح:** "۳۲۸" (۶۷.۳ درصد موفقیت) with purple dartboard icon.
    2. **تعداد تست:** "۴۸۶" (▲ ۱۵٪ نسبت به هفته قبل) with green checkmark icon.
    3. **تعداد آزمون:** "۶" (۴ آزمون) with blue document icon.
    4. **تست غلط:** "۱۵۸" (▼ ۸٪ نسبت به هفته قبل) with orange cross icon.
*   **Strengths & Weaknesses Tabs (`نقاط قوت` / `نقاط ضعف`):**
    *   Toggle between "نقاط قوت" (with green thumbs-up) and "نقاط ضعف" (with red thumbs-down).
    *   Linear progress bars with percentages for subject competencies (e.g., زیست شناسی ۸۴٪, شیمی ۷۸٪, ریاضی ۷۲٪).
    *   "مشاهده جزئیات >" interactive button.
*   **Study Time Distribution Chart (`توزیع زمان مطالعه در طول روز`):**
    *   Header with clock icon, section title, and total peak hour badge ("۳ ساعت").
    *   Smooth curve / line chart displaying hours per 4-hour intervals (۰-۴, ۴-۸, ۸-۱۲, ۱۲-۱۶, ۱۶-۲۰, ۲۰-۲۴) with purple dots and gradient fill.
*   **Periodic Reports Card (`گزارش‌های دوره‌ای`):**
    *   Illustration on the start side.
    *   Title "گزارش‌های دوره‌ای" and description.
    *   Action buttons: "دریافت گزارش" (download icon) and "مقایسه با دوستان" (group icon).
*   **Asset Relocation:**
    *   Move `ai-vector.png` from root to `app/src/main/res/drawable/ai_vector.png` and use it within the AI Analysis Card.
