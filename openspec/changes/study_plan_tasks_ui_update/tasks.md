# Tasks: Study Plan Tasks UI & Interaction Refinement

- [ ] **1. Presentation & Resources**
  - [ ] افزودن متن‌ها و رشته‌های فارسی مورد نیاز در `res/values/strings.xml` (مانند متن دیالوگ تایید انجام وظیفه).
  - [ ] بازطراحی هدر تقویم در `StudyPlanComponents.kt` / `StudyPlanScreen.kt`:
    - [ ] حذف نوار اسکرول روزهای هفته (Week Crawler Strip).
    - [ ] ایجاد نوار ناوبری روزانه با دکمه روز قبل در راست، تاریخ کامل و دکمه باز کردن تقویم در وسط، و دکمه روز بعد در چپ.
    - [ ] اتصال دیالوگ انتخاب تاریخ (DatePickerDialog) به تاریخ وسط.
  - [ ] ویرایش کارت‌های وظایف در `StudyPlanComponents.kt`:
    - [ ] حذف آیکون Bookmark / Save از کنار سه نقطه.
    - [ ] تغییر چیدمان دکمه شروع به سمت راست (زیر باکس نام درس).
    - [ ] قرار دادن دکمه سه‌نقطه و دایره وضعیت در سمت چپ.
  - [ ] افزودن دیالوگ تایید اتمام وظیفه (`TaskCompletionConfirmDialog`) و اتصال آن به کلیک دایره وضعیت.

- [ ] **2. ViewModel & Sorting Logic**
  - [ ] افزودن اکشن‌های `goToPreviousDay()`, `goToNextDay()`, `selectDate(LocalDate)` در `StudyPlanViewModel`.
  - [ ] اطمینان از انتقال تسک‌های تیک‌خورده به انتهای لیست روزانه.

- [ ] **3. Verification & Testing**
  - [ ] به‌روزرسانی و افزودن تست‌های واحد و کامپوز برای بررسی ناوبری تاریخ، حذف بوک‌مارک، چیدمان جدید دکمه‌ها و دیالوگ تایید انجام وظیفه.
  - [ ] اجرای بیلد و تست‌ها با `compile_applet` و `gradle :app:testDebugUnitTest`.
