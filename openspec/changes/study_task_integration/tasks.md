# Tasks: Study Task Integration & Implementation Checklist

## 1. Data Models, DTOs & Mappers
- [ ] **1.1. Network DTOs:** تعریف مدل‌های انتقال داده شبکه برای کاتالوگ، تسک‌های روزانه، ایجاد تسک دستی، ویرایش، خلاصه پیشرفت و آبجکت وضعیت اجرا (`execution`).
- [ ] **1.2. Domain Models:** پیاده‌سازی مدل‌های پایدار دامنه (`StudyTaskItem`, `StudySummary`, `StudyCatalog`, `BookItem`, `TopicItem`).
- [ ] **1.3. Date Mapper Service:** پیاده‌سازی سرویس تبدیل دوطرفه تاریخ میلادی استاندارد (`YYYY-MM-DD`) به تقویم شمسی جلالی (`YYYY/MM/DD`) با محاسبه دقیق سال‌های کبیسه و استخراج روزهای هفته به زبان فارسی.

## 2. Data & Network Layer
- [ ] **2.1. API Service Endpoints:** تعریف متدهای شبکه برای دریافت کاتالوگ (`GET /study-tasks/me/catalog`)، دریافت تسک‌های روزانه (`GET /study-tasks/me?date=`)، ساخت تسک دستی (`POST /study-tasks/me/manual`)، ویرایش (`PATCH /study-tasks/me/manual/{taskId}`) و حذف (`DELETE /study-tasks/me/manual/{taskId}`).
- [ ] **2.2. Study Task Repository:** پیاده‌سازی اینترفیس و پیاده‌سازی مخزن داده همراه با مدیریت خطاهای شبکه و کش موقت (In-Memory) برای کاتالوگ درسی.

## 3. Domain Layer & Use Cases
- [ ] **3.1. GetStudyCatalogUseCase:** واکشی کاتالوگ دروس با مکانیزم کش موقت.
- [ ] **3.2. GetDailyStudyTasksUseCase:** دریافت تسک‌ها و نگاشت تاریخ‌های میلادی به شمسی برای ارائه به لایه نمایش.
- [ ] **3.3. CreateManualTaskUseCase:** اعتبارسنجی مقادیر فرم، تولید شناسه یکتای Client-side UUID (`requestId`) و ارسال درخواست ثبت تسک.
- [ ] **3.4. UpdateManualTaskUseCase & DeleteManualTaskUseCase:** بررسی شرط عدم اجرا (`execution == null`) قبل از ارسال درخواست ویرایش یا حذف.

## 4. Presentation Layer & State Management
- [ ] **4.1. UI State Definition:** یکپارچه‌سازی `StudyPlanUiState` با لیست تسک‌های زنده، وضعیت لودینگ، خطاهای احتمالی، خلاصه روزانه و پرچم‌های قفل عملیات.
- [ ] **4.2. ViewModel Implementation:** هندل کردن Intentهای کاربر (انتخاب تاریخ در تقویم، بارگذاری مجدد، ثبت تسک، حذف و ویرایش) بر پایه الگوی UDF.
- [ ] **4.3. Business Lock Rule:** محاسبه فیلدهای `isEditable` و `isDeletable` بر اساس تهی بودن `execution` برای هر تسک.

## 5. UI Integration & Compose Screens
- [ ] **5.1. Calendar Binding:** اتصال انتخاب تاریخ در کامپوننت تقویم به بارگذاری تسک‌های همان روز با تبدیل لحظه‌ای تاریخ شمسی به میلادی.
- [ ] **5.2. Task Card UI:** نمایش کارت‌های تسک با عنوان درس، مبحث، تعداد و زمان پارت‌ها و وضعیت اجرا (در حال مطالعه / تکمیل شده).
- [ ] **5.3. Action Buttons & Execution Lock:** مخفی‌سازی یا غیرفعال‌سازی دکمه‌های حذف و ویرایش برای تسک‌های دارای آبجکت `execution`.
- [ ] **5.4. Create & Edit Task Dialog/Sheet:** اتصال فرم ساخت تسک به کاتالوگ دریافتی برای انتخاب پویای کتاب و سرفصل‌ها با امکان تعیین تعداد و دقایق دوره‌ها.
- [ ] **5.5. Empty & Loading States:** نمایش انیمیشن لودینگ (Shimmer) در حین واکشی اطلاعات و کارت راهنما در زمان خالی بودن لیست تسک‌ها.

## 6. Verification & Automated Testing
- [ ] **6.1. Date Mapping Unit Tests:** تست دقیق توابع تبدیل تاریخ شمسی به میلادی و برعکس در سناریوهای مرزی (تغییر ماه، سال کبیسه).
- [ ] **6.2. ViewModel & UseCase Unit Tests:** تست تولید شناسه یکتای Idempotency، صحت قفل ویرایش/حذف در زمان وجود `execution` و استیت‌های لودینگ/موفقیت/خطا.
- [ ] **6.3. Compose Screen & Robolectric Tests:** تست تعامل با تقویم، تغییر روز، رندر شدن تسک‌ها و باز شدن فرم ساخت تسک.
