# سند طراحی معماری و رابط کاربری (Design): صفحه «وظایف برنامه» (Study Plan Feature)

## ۱. ساختار معماری و جریان داده (Architecture & Data Flow)

این ماژول طبق استانداردهای کلین آرکیتکچر (Clean Architecture) در لایه `com.example.ui.features.studyplan` و مبتنی بر الگوی **MVVM** و **Unidirectional Data Flow (UDF)** طراحی شده است.

```
       ┌──────────────────────────────────────────────────────────┐
       │                سرور و سرویس‌های شبکه (REST API)          │
       │    GET /study-tasks/me?date=...                          │
       │    GET /study-tasks/me/catalog                           │
       │    POST /study-tasks/me/manual                           │
       │    POST /study-execution/me/...                          │
       └─────────────────────────────┬────────────────────────────┘
                                     │
                                     ▼ (safeApiCall)
       ┌──────────────────────────────────────────────────────────┐
       │             لایه داده و مخزن (StudyPlan Repository/Api)   │
       │        ApiClient.apiService -> Retrofit Execution        │
       └─────────────────────────────┬────────────────────────────┘
                                     │
                                     ▼
       ┌──────────────────────────────────────────────────────────┐
       │            لایه منطق و وضعیت (StudyPlanViewModel)         │
       │  - مدیریت استیت یکپارچه StudyPlanUiState                 │
       │  - فیلتر کردن تسک‌ها (همه، در حال انجام، نشده، انجام شده)│
       │  - مرتب‌سازی بر اساس اولویت/زمان                         │
       │  - ارسال رویدادهای شروع/تکمیل مطالعه                     │
       │  - ایجاد تسک دستی با فرم اعتبارسنجی‌شده                  │
       └─────────────────────────────┬────────────────────────────┘
                                     │
                   StateFlow<StudyPlanUiState> (Immutable)
                                     │
                                     ▼
       ┌──────────────────────────────────────────────────────────┐
       │              لایه نمایش (StudyPlanScreen & Composables)   │
       │  ├── StudyPlanTopHeader (آواتار، وضعیت، عنوان، اعلان‌ها)  │
       │  ├── StudyPlanSummaryCard (ماتریس ۴تایی آمار + نوار پیشرفت)│
       │  ├── StudyPlanFilterRow (پیل‌های انتخاب وضعیت)           │
       │  ├── RemainingTasksHeader (عنوان بخش، بج تعداد، مرتب‌سازی)│
       │  ├── StudyTaskItemCard (کارت تسک، نشانک، دکمه شروع/ادامه) │
       │  ├── CompletedTasksSection (لیست تسک‌های انجام‌شده با تیک)│
       │  ├── StudyPlanEmptyState & SkeletonLoader (لودینگ/خالی)  │
       │  └── AddTaskFloatingActionButton (دکمه شناور پایین سمت چپ)│
       └──────────────────────────────────────────────────────────┘
```

---

## ۲. مشخصات و استایل مؤلفه‌های رابط کاربری (UI Component Specifications)

### ۲.۱. هدر صفحه (`StudyPlanTopHeader`)
- **ساختار:** یک ردیف افقی با `statusBarsPadding()` و حاشیه افقی `20.dp`.
- **سمت راست (Start in RTL):** آواتار دایره‌ای کاربر (`size = 46.dp`) با قاب محو و در گوشه پایین سمت راست آن نشانگر وضعیت فعال (دایره سبز رنگ با تیک سفید `size = 14.dp`).
- **مرکز (Center):** 
  - عنوان اصلی: «وظایف برنامه» با تایپوگرافی برجسته (`IranSansFontFamily`, `FontWeight.Bold`, `fontSize = 20.sp`, رنگ تیره `#172353`).
  - زیرعنوان: «کارهای امروزت را مدیریت کن» با رنگ خاکستری مایل به سرمه‌ای (`#7E859E`, `fontSize = 13.sp`).
- **سمت چپ (End in RTL):** دکمه دایره‌ای نوتیفیکیشن (`size = 46.dp`) با پس‌زمینه سفید، کادر خاکستری ملایم (`#F0F2F7`)، آیکون زنگوله با نشانگر بنفش در بالا سمت راست آن.

### ۲.۲. کارت خلاصه وضعیت مطالعه (`StudyPlanSummaryCard`)
- **کانتینر:** کارت با پس‌زمینه سفید خالص (`#FFFFFF`)، گوشه‌های گرد (`24.dp`)، پدینگ داخلی `16.dp` و کادر ملایم (`#F1F3F9`).
- **ماتریس ۴ ستونه (ستون‌ها از راست به چپ در RTL):**
  1. **زمان مطالعه:** آیکون ساعت بنفش + متن «زمان مطالعه» + مقدار عددی فارسی برجسته «۴۵ دقیقه».
  2. **باقی‌مانده:** آیکون ساعت نارنجی + متن «باقی‌مانده» + مقدار عددی فارسی «۴».
  3. **انجام شده:** آیکون تیک سبز + متن «انجام شده» + مقدار عددی فارسی «۶».
  4. **کل تسک‌ها:** آیکون نشانک بنفش + متن «کل تسک‌ها» + مقدار عددی فارسی «۱۰».
  - *خطوط جداکننده:* خطوط عمودی ظریف و باریک (`#F2F4FA`) میان ستون‌ها.
- **نوار پیشرفت تجمیعی:**
  - نوار خطی با ارتفاع `8.dp` و گوشه‌های کاملاً گرد (`CircleShape`).
  - بخش پرشده با رنگ بنفش برند (`#7656F5`) و بخش خالی با رنگ خاکستری ملایم (`#ECEFF7`).
  - متن برچسب پیشرفت در سمت راست/چپ: «از ۱۰ تسک انجام شده».

### ۲.۳. ردیف فیلتر وضعیت‌ها (`StudyPlanFilterRow`)
- ردیف افقی متشکل از ۴ پیل (Chip) با فاصله `8.dp`:
  1. **«همه»:** پس‌زمینه سالید بنفش (`#7656F5`)، متن سفید ضخیم.
  2. **«در حال انجام»:** پس‌زمینه سفید، کادر خاکستری ملایم (`#E4E7F1`)، آیکون Play/ساعت، متن تیره.
  3. **«انجام نشده»:** پس‌زمینه سفید، کادر خاکستری، آیکون ساعت، متن تیره.
  4. **«انجام شده»:** پس‌زمینه سفید، کادر خاکستری، آیکون تیک سبز، متن تیره.

### ۲.۴. بخش کارهای باقی‌مانده (`RemainingTasksSection`)
- **هدر بخش:**
  - سمت راست (RTL): متن «کارهای باقی‌مانده» (Bold, `17.sp`) + بج دایره‌ای بنفش روشن (`#EDE9FE`) با عدد تعداد بنفش پررنگ (مثلاً «۴»).
  - سمت چپ (RTL): دکمه متنی با آیکون لیست و فلش «مرتب‌سازی ⌄».
- **کارت تسک استاندارد (`StudyTaskCard`):**
  - **کانتینر:** پس‌زمینه سفید، گوشه‌های گرد `20.dp`، کادر ملایم `#EFF1F7`، پدینگ `14.dp`.
  - **بخش راست (بج درس):** کانتینر مربعی با گوشه‌های گرد `16.dp` متناسب با درس:
    - *ریاضی:* پس‌زمینه یاسی روشن (`#F3F0FF`)، آیکون کتاب بنفش، عنوان «ریاضی» بنفش، زیرعنوان «رشته تجربی».
    - *فیزیک:* پس‌زمینه آبی روشن (`#EFF6FF`)، آیکون اتم آبی، عنوان «فیزیک» آبی، زیرعنوان «رشته تجربی».
    - *شیمی:* پس‌زمینه نارنجی روشن (`#FFF7ED`)، آیکون ارلن نارنجی، عنوان «شیمی» نارنجی، زیرعنوان «رشته تجربی».
    - *زیست‌شناسی:* پس‌زمینه سبز نعنایی (`#F0FDF4`)، آیکون برگ سبز، عنوان «زیست‌شناسی» سبز، زیرعنوان «رشته تجربی».
  - **بخش میانی (مشخصات تسک):**
    - عنوان تسک به صورت پررنگ (Bold `15.sp`).
    - نام فصل/مبحث با رنگ خاکستری متمایل به آبی (`#6B7280`).
    - ردیف متادیتا: زمان مطالعه با آیکون ساعت (مثلاً «۴۵ دقیقه»)، شماره دور مطالعه («دور ۲/۳»)، نشانگر اولویت ۴ خطی متناسب با درجه اهمیت.
    - در صورت وضعیت در حال اجرا: متن زمان سپری‌شده (مانند «۶۰ دقیقه از ۹۰ دقیقه») و نوار پیشرفت بنفش در زیر بخش میانی.
  - **بخش چپ (اقدامات):**
    - آیکون نشانک/بوکمارک در گوشه بالا.
    - دکمه پیل‌شکل بنفش با آیکون پلی و متن «شروع ▶» (یا دکمه با کادر بنفش و متن «ادامه ▶» برای موارد در حال اجرا).

### ۲.۵. بخش انجام‌شده‌ها (`CompletedTasksSection`)
- **هدر بخش:** عنوان «انجام‌شده‌ها» با بج سبز روشن (`#DCFCE7`) و عدد سبز + لینک ناوبری «مشاهده همه ‹».
- **کارت‌های تکمیل‌شده:**
  - کانتینر سفید با خط کادر ملایم.
  - سمت چپ: بج سبز رنگ با آیکون تیک سبز دایره‌ای و برچسب «انجام شد».
  - مرکز: عنوان و فصل و زمان مطالعه با آیکون ساعت خاکستری.
  - سمت راست: بج درس مربوطه.

### ۲.۶. موقعیت و هندسه دکمه شناور افزودن تسک (Floating Action Button)
- **شکل و رنگ:** دکمه دایره‌ای با قطر `58.dp`، رنگ پس‌زمینه گرادینت/سالید بنفش (`#7656F5`)، آیکون علامت مثبت (`Icons.Default.Add`) سفید با اندازه `28.dp`، و سایه برجسته نرم بنفش (`shadow(8.dp, CircleShape, spotColor = Color(0x667656F5))`).
- **محل قرارگیری دقیق (Precise Alignment):**
  - تراز شده در **گوشه پایین سمت چپ (Bottom-Left)** صفحه با استفاده از:
    `Modifier.align(Alignment.BottomStart)` (با توجه به این که در چینش RTL، سمت چپ برابر با انتهای افقی صفحه یا `BottomStart`/`BottomEnd` متناسب با جهت چیدمان است؛ لذا صراحتاً در جهت Absolute Left با `padding(start = 20.dp, bottom = 90.dp)` تنظیم می‌شود).
  - فاصله عمودی مناسب از پایین صفحه (`bottom = 90.dp`) جهت قرارگیری بدون تداخل در بالای نوار ناوبری شناور (`ShetabBottomNavigation`).

---

## ۳. ساختار داده‌ها و مدل‌های API (Data Models & DTOs)

### ۳.۱. مدل‌های ورودی و خروجی شبکه (Network DTOs)

```kotlin
// خلاصه وضعیت روزانه
data class StudyTaskSummaryDto(
    val total: Int = 0,
    val completed: Int = 0,
    val pending: Int = 0,
    val completionPercent: Int = 0,
)

// جزئیات تسک مطالعه
data class StudyTaskDto(
    val id: String,
    val sourceType: String, // "MANUAL" or "SYSTEM_PLAN"
    val sourceId: String? = null,
    val planId: String? = null,
    val title: String,
    val book: StudyTaskNamedRefDto,
    val chapter: StudyTaskNamedRefDto,
    val topic: StudyTaskNamedRefDto,
    val scheduledOn: String,
    val periodCount: Int = 1,
    val minutesPerPeriod: Int,
    val plannedMinutes: Int,
    val activityType: String? = null,
    val sequence: Int = 0,
    val execution: StudyTaskExecutionDto? = null,
)

// وضعیت اجرای تسک
data class StudyTaskExecutionDto(
    val id: String,
    val status: String, // "NOT_STARTED", "ACTIVE", "PAUSED", "COMPLETED"
    val eventSequence: Int = 0,
    val activeSeconds: Int = 0,
    val completionPercent: Int? = null,
    val startedAt: String? = null,
    val activeSinceAt: String? = null,
    val stoppedAt: String? = null,
    val finishedAt: String? = null,
)

// لیست و آمار روزانه
data class DailyStudyTasksBodyDto(
    val date: String,
    val items: List<StudyTaskDto> = emptyList(),
    val summary: StudyTaskSummaryDto = StudyTaskSummaryDto(),
)

// ایجاد تسک دستی جدید
data class CreateManualStudyTaskDto(
    val requestId: String,
    val topicId: String,
    val scheduledOn: String,
    val periodCount: Int,
    val minutesPerPeriod: Int,
)
```

---

## ۴. مدیریت وضعیت صفحه (State Management & UI States)

وضعیت صفحه در یک دیتاکلاس تغییرناپذیر کپسوله شده و از طریق `StateFlow` به UI جریان می‌یابد:

```kotlin
enum class StudyTaskFilter {
    ALL,        // همه
    IN_PROGRESS,// در حال انجام
    PENDING,    // انجام نشده
    COMPLETED   // انجام شده
}

enum class StudyTaskSortOrder {
    DEFAULT,    // ترتیب برنامه
    DURATION,   // بر اساس مدت زمان
    PRIORITY    // بر اساس اولویت
}

data class StudyPlanUiState(
    val selectedDate: LocalDate = LocalDate.now(ZoneId.of("Asia/Tehran")),
    val catalog: StudyTaskCatalogBodyDto? = null,
    val day: DailyStudyTasksBodyDto? = null,
    val selectedFilter: StudyTaskFilter = StudyTaskFilter.ALL,
    val sortOrder: StudyTaskSortOrder = StudyTaskSortOrder.DEFAULT,
    val loading: Boolean = true,
    val refreshing: Boolean = false,
    val busyTaskId: String? = null,
    val creating: Boolean = false,
    val showAddDialog: Boolean = false,
    val error: String? = null,
    val mutationMessage: String? = null,
) {
    val totalTasks: Int get() = day?.summary?.total ?: day?.items?.size ?: 0
    val completedTasks: Int get() = day?.summary?.completed ?: day?.items?.count { it.isCompleted } ?: 0
    val remainingTasks: Int get() = day?.summary?.pending ?: (totalTasks - completedTasks).coerceAtLeast(0)
    val totalStudyMinutes: Int get() = day?.items?.sumOf { it.plannedMinutes } ?: 0
    val progressFraction: Float get() = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f

    val remainingItems: List<StudyTaskDto>
        get() = day?.items?.filter { !it.isCompleted } ?: emptyList()

    val completedItems: List<StudyTaskDto>
        get() = day?.items?.filter { it.isCompleted } ?: emptyList()

    val filteredItems: List<StudyTaskDto>
        get() {
            val base = when (selectedFilter) {
                StudyTaskFilter.ALL -> day?.items ?: emptyList()
                StudyTaskFilter.IN_PROGRESS -> day?.items?.filter { it.isInProgress } ?: emptyList()
                StudyTaskFilter.PENDING -> day?.items?.filter { it.isPending } ?: emptyList()
                StudyTaskFilter.COMPLETED -> day?.items?.filter { it.isCompleted } ?: emptyList()
            }
            return when (sortOrder) {
                StudyTaskSortOrder.DEFAULT -> base
                StudyTaskSortOrder.DURATION -> base.sortedByDescending { it.plannedMinutes }
                StudyTaskSortOrder.PRIORITY -> base.sortedByDescending { it.periodCount }
            }
        }
}
```

### ۴.۱. نحوه نمایش استیت‌های مختلف:
1. **حالت لودینگ (Skeleton Loading State):** نمایش ۴ کارت ساختگی شبیه به کارت‌های واقعی با گرادینت متحرک شیمر (Shimmer) جهت انتقال حس نرمی در اولین دریافت داده‌ها.
2. **حالت موفقیت (Success State):** نمایش آمار دقیق بالا، فیلترها، لیست کارهای باقی‌مانده و بخش انجام‌شده‌ها با امکان تعامل کامل.
3. **حالت خطا (Error State):** نمایش کامپوننت خطا با آیکون قطعی شبکه، پیام خطا به زبان فارسی و دکمه «تلاش مجدد» (Retry).
4. **حالت خالی (Empty State):** هنگامی که برای تاریخ انتخابی برنامه‌ای ثبت نشده باشد، یک تصویر/تصویرسازی مناسب، متن راهنمای «هنوز برنامه‌ای برای امروز نداری» و دکمه‌های «افزودن تسک دستی» یا «ساخت برنامه هوشمند» نمایش داده می‌شود.

---

## ۵. استانداردهای RTL و تایپوگرافی فارسی
- اعمال سراسری `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl)`.
- عدم استفاده از مقادیر `left`/`right` در مودیفایرها و بهره‌گیری اختصاصی از `start`/`end`.
- تنظیم فونت `IranSansFontFamily` با وزن‌های `Bold` برای عناوین و `Medium`/`Regular` برای متن‌ها و متادیتا.
- اعمال تابع تبدیل ارقام انگلیسی به فارسی (`toPersianNumber()`) برای تمامی فیلدهای عددی و زمان‌ها.
