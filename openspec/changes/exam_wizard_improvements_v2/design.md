# Design: Exam Wizard UI Improvements V2

## 1. Component Architecture & Changes

### A. Step 1: `Step1ExamStructureScreen.kt`
- **Reordering Sections:**
  1. Header / Intro
  2. **نوع آزمون** (Exam Type: تستی / تشریحی)
  3. **منبع سوالات** (Question Source: تألیفی / کنکور / نهایی) -> جابجایی به زیر نوع آزمون
  4. **پایه و رشته** (Grade & Major dropdowns)
  5. **کتاب‌ها و محدوده آزمون** (Books & Inline Add Book)
  6. **دکمه‌های اقدام:**
     - دکمه «ادامه به مرحله بعد»: حذف آیکون فلش Arrow.
     - دکمه «انصراف»: تغییر رنگ به قرمز (`Color(0xFFEF4444)` / `colors.error`).

### B. Step 2: `Step2QuestionSettingsScreen.kt`
- **RTL & Persian Design System Enforcement:**
  - تضمین قرارگیری کامل در `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl)`.
  - **Summary Card (2 Rows):**
    - **Row 1:**
      - ستون ۱: نوع آزمون (نشانگر تم بنفش/تستی)
      - ستون ۲: پایه (آیکون مدرسه + عنوان پایه)
      - ستون ۳: رشته (آیکون شاخه + عنوان رشته)
    - **Row 2:**
      - ستون ۱: تعداد کتاب‌ها (آیکون کتاب + تعداد به فارسی)
      - ستون ۲: منبع سوال (آیکون ویرایش/منبع + نام منبع)
  - **Book Question Cards:**
    - طراحی کارت با کادر ملایم، پس‌زمینه سفید، سایه‌روشن استاندارد.
    - در هدر کارت: عنوان کتاب و فصل در سمت راست همراه با جلد کتاب با گرادیانت، دکمه حذف قرمز در سمت چپ.
    - در بخش موضوعات: برچسب‌های تگ بنفش ملایم با متن منظم.
    - در بخش استپرهای ۴ گانه دشواری (آسان، متوسط، دشوار، خیلی دشوار): فاصله‌گذاری مناسب، دکمه‌های + و - کامپکت، نمایش اعداد با فونت ایران‌سنس.
  - **Navigation Buttons:**
    - دکمه «ادامه به مرحله بعد»: حذف آیکون فلش، فقط متن.
    - دکمه «بازگشت»: رنگ و استایل متناسب.

## 2. Typography & Colors
- **Font:** `IranSansFontFamily`
- **Persian Numbers:** تبدیل تمام اعداد انگلیسی به ارقام فارسی از طریق `toPersianNumber()`.
- **Cancel Button Colors:** `Color(0xFFEF4444)` / `Color(0xFFFEE2E2)`
