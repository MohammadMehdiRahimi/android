# Design: Study Plan Tasks UI & Interaction Refinement

## 1. Architectural & Component Changes

### 1.1 Calendar Bar Redesign (`StudyPlanHeaderBar` / `DateNavigationHeader`)
- **حذف کامپوننت اسکرول هفته (`WeekStrip` / `HorizontalCalendarCrawler`)**: حذف ردیف روزهای ۷ گانه.
- **کامپوننت جدید ناوبری تاریخ**:
  - ساختار `Row` با تراز افقی وسط و `SpaceBetween` در کانتینر سفید با گوشه‌های گرد و سایه ملایم.
  - **سمت راست (در RTL):** دکمه آیکون فلش راست (`Icons.AutoMirrored.Filled.ArrowForward` یا فلش راست) جهت انتقال به روز قبل (`onPreviousDay`).
  - **مرکز:** نام روز هفته و تاریخ کامل شمسی (مثلاً «سه‌شنبه، ۴ شهریور ۱۴۰۵») با آیکون تقویم و کلیک‌پذیر (`clickable`) برای باز کردن دیالوگ تقویم شمسی/میلادی.
  - **سمت چپ (در RTL):** دکمه آیکون فلش چپ (`Icons.AutoMirrored.Filled.ArrowBack` یا فلش چپ) جهت انتقال به روز بعد (`onNextDay`).

### 1.2 Task Card Layout & Actions (`TaskCardItem` / `StudyPlanComponents.kt`)
- **حذف آیکون ذخیره:** حذف کامپوننت/آیکون `Icons.Outlined.Bookmark` یا `Icons.Filled.Bookmark`.
- **طراحی ردیف اکشن‌های پایین کارت (Action Bar):**
  - چیدمان در قالب یک `Row` با `fillMaxWidth()`:
    - **سمت راست (Start در RTL):** دکمه «شروع» با استایل کپسولی/بنفش (`PlanPurple` / `#7656F5`) با آیکون پخش (`Icons.Default.PlayArrow`) که مستقیماً زیر باکس عنوان درس قرار می‌گیرد.
    - **سمت چپ (End در RTL):**
      - منوی گزینه‌های بیشتر (`MoreVert` / سه‌نقطه).
      - چک‌باکس دایره‌ای وضعیت (`CircleCheckbox`):
        - در حالت انجام‌نشده: دایره توخالی با بوردر خاکستری/بنفش ملایم.
        - در حالت انجام‌شده: دایره سبز پررنگ (`#10B981`) همراه با آیکون تیک سفید (`Icons.Default.Check`).

### 1.3 Task Completion Confirmation Dialog
- در صورت کلیک روی دایره وضعیت برای یک تسک انجام‌نشده:
  - استیت محلی/ویومدل `confirmTaskCompletion: StudyTaskUiItem?` فعال می‌شود.
  - دیالوگ استاندارد `AlertDialog` با استایل المان‌های شتاب باز می‌شود:
    - **عنوان:** «تایید انجام وظیفه»
    - **متن:** «آیا مطمئن هستید که می‌خواهید این وظیفه را به عنوان «انجام شده» ثبت کنید؟»
    - **دکمه تایید:** «بله، انجام شد» (با رنگ سبز یا بنفش اصلی)
    - **دکمه انصراف:** «انصراف»
  - با تایید: فراخوانی اکشن `toggleTaskDone` در ویومدل، تیک سبز زدن و مرتب‌سازی/دسته‌بندی تسک‌های انجام‌شده در انتهای لیست.

## 2. State & Data Flow
- `StudyPlanViewModel`:
  - اضافه شدن متدهای ناوبری روز: `goToPreviousDay()` و `goToNextDay()`.
  - متد `setSpecificDate(localDate)`.
  - متد تکمیل تسک و مرتب‌سازی لیست وظایف به گونه‌ای که تسک‌های `isCompleted == true` در انتهای ردیف یا در بخش تکمیل‌شده‌ها قرار گیرند.
