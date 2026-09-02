# Proposal: Exam Entry Screen UI Redesign

## 1. Description
The user provided a specific screenshot design for the "Exam Entry / Details" screen (`ورود به آزمون`).
When a student searches/enters an Exam ID, they arrive at this screen which displays the Exam ID card, the detailed Exam Specifications card, an informational note, and the prominent "ورود به آزمون" (Enter Exam) CTA button.

## 2. Acceptance Criteria (Matching Provided UI)
*   **Header:**
    *   Top Bar with circular Back button on the left (RTL aligned).
    *   Header title: "ورود به آزمون" with a clipboard icon.
    *   Subtitle: "با وارد کردن شناسه آزمون، وارد آزمون شوید".
*   **Card 1: Exam ID Input / Display (`شناسه آزمون`):**
    *   Section title with purple dot indicator: `• شناسه آزمون`.
    *   Rounded container with:
        *   "ID" badge tag on the start.
        *   The Exam ID text (editable/searchable or displaying the entered ID in Persian numbers).
        *   A scanner / QR icon button on the end.
    *   Helper text beneath: "شناسه آزمون را از مدرس یا برگزارکننده دریافت کنید.".
*   **Card 2: Exam Specifications (`مشخصات آزمون`):**
    *   Section title with purple dot indicator: `• مشخصات آزمون`.
    *   Top banner with exam illustration, title ("آزمون زیست شناسی دهم" or dynamic exam title), and a pill badge ("آزمون جامع فصل ۱ تا ۳").
    *   Divider-separated rows with light purple icon boxes on the end and values on the start:
        1. **برگزارکننده:** استاد احمدی (Person icon)
        2. **تاریخ برگزاری:** جمعه ۲۴ خرداد ۱۴۰۳ (Calendar icon)
        3. **ساعت شروع:** ۱۰:۰۰ صبح (Clock icon)
        4. **مدت زمان:** ۹۰ دقیقه (Hourglass icon)
        5. **تعداد سوالات:** ۴۰ سوال (Checklist icon)
        6. **نوع سوالات:** چهارگزینه‌ای (Help/Question icon)
        7. **نمره کل:** ۴۰ نمره (Star icon)
*   **Bottom Warning Box (`توجه داشته باشید`):**
    *   Light purple/lavender container with Info icon.
    *   Header: "توجه داشته باشید".
    *   Notice: "پس از شروع آزمون، امکان خروج وجود ندارد و زمان آزمون شروع خواهد شد.".
*   **Bottom CTA Button:**
    *   Full-width purple button with text "ورود به آزمون" and login/arrow icon.
    *   Navigates directly to the exam taking screen (`exam_taking`).
