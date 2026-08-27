# Design: Study Task Integration & Architecture Specification

## 1. Architectural Architecture & Principles
سیستم بر اساس الگوی **معماری تمیز (Clean Architecture)** و الگوی **جریان داده یک‌طرفه (Unidirectional Data Flow - UDF)** طراحی شده است. لایه‌ها کاملاً از یکدیگر تفکیک شده و وظایف مشخصی دارند:

```text
┌────────────────────────────────────────────────────────┐
│                   Presentation Layer                   │
│   • StudyPlanScreen (RTL UI / Calendar / Task Cards)   │
│   • CreateTaskSheet / EditTaskModal                    │
│   • StudyPlanViewModel (UiState & Event Reducers)      │
└───────────────────────────▲────────────────────────────┘
                            │ (Observes State / Dispatches Intents)
┌───────────────────────────┴────────────────────────────┐
│                      Domain Layer                      │
│   • GetStudyCatalogUseCase                             │
│   • GetDailyStudyTasksUseCase                          │
│   • CreateManualTaskUseCase                            │
│   • UpdateManualTaskUseCase                            │
│   • DeleteManualTaskUseCase                            │
│   • DateTransformService (Gregorian <-> Jalali Mapper) │
└───────────────────────────▲────────────────────────────┘
                            │ (Calls Repositories)
┌───────────────────────────┴────────────────────────────┐
│                   Data & Network Layer                 │
│   • StudyTaskRepository & Implementation               │
│   • StudyTaskApiService (Endpoints & DTOs)             │
│   • Local Storage / In-Memory Catalog Cache            │
└────────────────────────────────────────────────────────┘
```

---

## 2. Unified Date & Time Architecture (مدیریت تبدیل دوطرفه تاریخ)

### ۲.۱. منطق ایزولاسیون و تبدیل در لایه Mapper / Domain
برای جلوگیری از پراکندگی منطق تبدیل تاریخ در کامپوننت‌های بصری، یک ماژول تبدیل تاریخ متمرکز تعبیه می‌شود:

1. **نمای بیرونی و لایه شبکه (Server Boundary):**
   - فرمت ورودی و خروجی شبکه: همیشه رشته استاندارد میلادی بر مبنای `YYYY-MM-DD` (به عنوان مثال `2026-08-25`).
2. **نمای درونی و لایه کاربر (Client UI Boundary):**
   - تمامی کامپوننت‌های تقویم، برچسب‌های روزانه و فیلدهای تاریخ در UI، ساختار شمسی (`سال/ماه/روز` به عنوان مثال `۱۴۰۵/۰۶/۰۳`) و اسامی روزهای هفته (شنبه، یکشنبه، ...) را دریافت می‌کنند.
3. **تبدیل رفت (User Input -> Server Payload):**
   - کاربر در تقویم یا فرم، یک روز شمسی را انتخاب می‌کند.
   - لایه Presentation تاریخ شمسی انتخابی را به لایه منطق تحویل می‌دهد.
   - تابع تبدیل (`JalaliToGregorianMapper`) تاریخ را به `YYYY-MM-DD` میلادی تبدیل کرده و در درخواست شبکه (`scheduledOn` یا پارامتر `date`) قرار می‌دهد.
4. **تبدیل برگشت (Server Response -> UI Presentation):**
   - سرور فیلدهای تاریخ تسک (`scheduledOn`، `createdAt` و تاریخ‌های `execution`) را به صورت میلادی بازمی‌گرداند.
   - هنگام نگاشت مدل شبکه (DTO) به مدل دامنه و UI (`TaskItemUiModel`)، تابع تبدیل (`GregorianToJalaliMapper`) تاریخ را به تاریخ معادل شمسی با اعداد فارسی و متن‌های خوانا (مانند «امروز»، «فردا» یا «۳ شهریور») تبدیل می‌کند.

---

## 3. Data Models & API Contracts

### ۳.۱. مدل کاتالوگ منابع (`GET /study-tasks/me/catalog`)
- **خروجی سرور:** لیستی از دروس شامل شناسه و عنوان کتاب‌ها (`books`)، فصل‌ها (`chapters`) و مباحث (`topics`).
- **استراتژی کش کلاینت:** از آنجا که کاتالوگ درسی در طول یک نشست کاربری به ندرت تغییر می‌کند، داده‌های کاتالوگ در حافظه موقت (In-Memory Cache) نگهداری می‌شوند تا با هر بار فشردن دکمه (+) درخواست تکراری به سرور ارسال نشود.

### ۳.۲. دریافت تسک‌های روزانه (`GET /study-tasks/me?date={YYYY-MM-DD}`)
- **پارامتر ارسالی:** `date` به فرمت `YYYY-MM-DD` (محاسبه‌شده از تاریخ انتخابی در تقویم شمسی).
- **مدل پاسخ:**
  ```json
  {
    "items": [
      {
        "id": "task-uuid",
        "topicId": "topic-uuid",
        "topicTitle": "مشتق و کاربرد آن",
        "bookTitle": "حسابان ۲",
        "scheduledOn": "2026-08-25",
        "periodCount": 2,
        "minutesPerPeriod": 45,
        "totalMinutes": 90,
        "isManual": true,
        "execution": {
          "status": "IN_PROGRESS", // یا COMPLETED / PAUSED
          "startedAt": "2026-08-25T08:30:00Z",
          "completedPeriods": 1,
          "spentMinutes": 45
        }
      }
    ],
    "summary": {
      "totalTasks": 4,
      "completedTasks": 1,
      "totalMinutesPlanned": 240,
      "totalMinutesSpent": 60,
      "progressPercentage": 25
    }
  }
  ```

### ۳.۳. ایجاد تسک جدید دستی (`POST /study-tasks/me/manual`)
- **ساختار بدنه ارسالی:**
  ```json
  {
    "requestId": "550e8400-e29b-41d4-a716-446655440000",
    "topicId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "scheduledOn": "2026-08-25",
    "periodCount": 3,
    "minutesPerPeriod": 45
  }
  ```
- **قانون کلاینتی Idempotency:**
  - قبل از ارسال هر درخواست، یک شناسه یکتای تصادفی (UUID v4) برای `requestId` تولید می‌شود. در صورت تلاش مجدد به دلیل قطعی موقت اینترنت، همان `requestId` مجدداً ارسال می‌گردد تا از ثبت تسک تکراری در سمت سرور جلوگیری شود.

### ۳.۴. ویرایش و لغو تسک (`PATCH` و `DELETE`)
- **ویرایش:** `PATCH /study-tasks/me/manual/{taskId}` همراه با مقادیر اصلاح‌شده `topicId`، `scheduledOn`، `periodCount`، `minutesPerPeriod`.
- **حذف:** `DELETE /study-tasks/me/manual/{taskId}` جهت لغو تسک.

---

## 4. Business Logic & Execution Lock Rule

### ۴.۱. قفل منطقی تسک‌های در حال اجرا یا انجام‌شده
- **قاعده اساسی:** بررسی آبجکت `execution` در هر تسک.
- **منطق تصمیم‌گیری در لایه Presentation / Model:**
  ```text
  isEditable = (task.execution == null)
  isDeletable = (task.execution == null)
  ```
- **رفتار در UI:**
  - اگر `execution == null`: آیکون یا منوی سه نقطه ویرایش و حذف فعال بوده و کاربر می‌تواند تسک را ویرایش یا حذف کند.
  - اگر `execution != null`:
    - دکمه‌ها و گزینه‌های ویرایش و حذف در منوی کارت تسک کاملاً غیرفعال (Disabled) یا مخفی (Hidden) می‌شوند.
    - وضعیت جاری تسک (مانند «در حال مطالعه»، «پایان یافته» یا درصد پیشرفت پارت‌ها) جایگزین منوی تغییرات می‌شود.
    - در صورت کلیک احتمالی، پیام مناسبی به کاربر نمایش داده می‌شود: «این تسک شروع شده یا به اتمام رسیده است و امکان تغییر یا حذف آن وجود ندارد.»

---

## 5. Client State Management & UI State Flow

### ۵.۱. ساختار وضعیت صفحه (`StudyPlanUiState`)
وضعیت صفحه یک مدل داده‌ای تغییرناپذیر (Immutable) است که وضعیت کل صفحه را به شکل صریح توصیف می‌کند:

```text
data class StudyPlanUiState(
    val selectedJalaliDate: JalaliDate,
    val selectedGregorianDateIso: String,
    val calendarDays: List<CalendarDayUiModel>,
    val isLoadingTasks: Boolean = false,
    val isActionInProgress: Boolean = false,
    val tasks: List<StudyTaskItemUiModel> = emptyList(),
    val summary: StudySummaryUiModel? = null,
    val isCatalogLoading: Boolean = false,
    val catalog: StudyCatalogUiModel? = null,
    val isCreateSheetOpen: Boolean = false,
    val taskBeingEdited: StudyTaskItemUiModel? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
```

### ۵.۲. چرخه رخدادها و جریان داده (User Intents -> Reducers):
1. **`OnDateSelected(jalaliDate)`:**
   - تاریخ شمسی فعال تغییر می‌یابد.
   - تاریخ معادل میلادی محاسبه می‌شود.
   - استیت به `isLoadingTasks = true` تغییر می‌یابد.
   - درخواست `GET /study-tasks/me?date={isoDate}` ارسال می‌شود.
   - در صورت موفقیت: `tasks` و `summary` آپدیت شده و `isLoadingTasks = false` می‌شود.
   - در صورت عدم وجود تسک: استیت با لیست خالی مشخص شده و کارت وضعیت «برای این روز برنامه‌ای ثبت نشده است» نمایش داده می‌شود.
2. **`OnOpenCreateTaskClicked`:**
   - اگر کاتالوگ هنوز واکشی نشده باشد، `GET /study-tasks/me/catalog` فراخوانی می‌شود.
   - فرم/شیت ساخت تسک جدید باز می‌شود.
3. **`OnSubmitManualTask(form)`:**
   - اعتبارسنجی مقادیر فرم (انتخاب موضوع، تاریخ معتبر، تعداد دوره بیشتر از صفر).
   - تولید `requestId` یکتا.
   - تبدیل تاریخ انتخابی فرم به فرمت میلادی.
   - فراخوانی `POST /study-tasks/me/manual`.
   - پس از موفقیت: بستن فرم، نمایش پیام موفقیت و فراخوانی مجدد تسک‌های همان روز جهت رفرش خودکار لیست.
4. **`OnDeleteTaskClicked(taskId)`:**
   - بررسی اعتبارسنجی اولیه `execution == null`.
   - فراخوانی `DELETE /study-tasks/me/manual/{taskId}`.
   - حذف مستقیم آیتم از لیست جاری یا رفرش داده‌ها.

---

## 6. Error Handling & Edge Cases

| وضعیت خطا / سناریو | منشأ خطا | رفتار کلاینت و بازخورد به کاربر |
| :--- | :--- | :--- |
| عدم دسترسی به اینترنت / خطای شبکه | Network / Timeout | نمایش بنر تلاش مجدد بدون از دست رفتن تاریخ انتخابی کاربر |
| خطای ۴۰۱ یا انقضای نشست | Authentication | ارجاع به لایه Refresh Token خودکار و در صورت شکست، هدایت به ورود |
| تلاش برای تغییر تسک در حال اجرا | 400 TASK_LOCKED | جلوگیری پیش‌دستانه در UI؛ در صورت وقوع از سمت سرور، نمایش پیام خطای شفاف |
| بازه تاریخی نامعتبر برای ثبت تسک | 400 DATE_OUT_OF_RANGE | نمایش پیام خطای فارسی در فرم («تاریخ انتخابی باید بین امروز تا ۳۰ روز آینده باشد») |
| مدت زمان بیش از حد مجاز | 400 DURATION_OVERFLOW | محدودسازی اسلایدرها/فیلدها در UI به حداکثر ۲۴ ساعت در روز |
| تلاش مکرر برای فشردن دکمه ثبت | Multiple Clicks | غیرفعال‌سازی دکمه در زمان ارسال (`isActionInProgress`) به همراه Idempotency UUID |
