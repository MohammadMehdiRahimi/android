# Tasks: Global Network Connection Error State & Retry Implementation

- [ ] **1. Core UI Components & Resources**
  - [ ] افزودن رشته‌های استاندارد خطا و دکمه تلاش مجدد در `res/values/strings.xml`.
  - [ ] ساخت کامپوننت مشترک `NetworkErrorView` در مسیر `app/src/main/java/com/example/ui/core/components/NetworkErrorView.kt` با استایل M3، پشتیبانی از حالت Fullscreen/Inline، آیکون مدرن، جهت‌گیری RTL و TestTags اختصاصی.

- [ ] **2. Feature Integration (صفحات دریافت‌کننده دیتا از سرور)**
  - [ ] **صفحه برنامه‌ریزی درسی (`StudyPlanScreen` & `StudyPlanViewModel`):** اضافه کردن کنترل عدم اتصال به شبکه و نمایش `NetworkErrorView` با متد بازخوانی `loadDayPlan()` و `refresh()`.
  - [ ] **صفحه ساخت برنامه مطالعاتی (`CreateStudyPlanScreen` & `CreateStudyPlanViewModel`):** اضافه کردن حالت عدم ارتباط هنگام شکست در دریافت کاتالوگ درسی (`loadCatalog()`).
  - [ ] **صفحه ثبت‌نام و دریافت رشته‌ها (`RegisterScreen` & `RegisterViewModel`):** اضافه کردن ویجت خطای شبکه با دکمه بارگذاری مجدد در صورت خطای اندپوینت `/base-info/onboarding`.
  - [ ] **سایر صفحات اصلی و فرعی مرتبط با سرور (`AcademicReportScreen`, `AcademicLeaderboardScreen`, `TicketsScreen`):** بررسی و جایگزینی ویوهای خالی/خطا با `NetworkErrorView`.

- [ ] **3. Unit & UI Testing**
  - [ ] نوشتن تست واحد برای اعتبارسنجی انتقال وضعیت به حالت خطای شبکه در ویومدل‌ها (`StudyPlanViewModelTest`, `RegisterViewModelTest`).
  - [ ] پیاده‌سازی تست رابط کاربری Compose (`NetworkErrorViewTest.kt`) جهت بررسی رندر صحیح المان‌ها، فونت فارسی، جهت RTL و عملکرد دکمه تلاش مجدد (`performClick`).
  - [ ] اعتبارسنجی بیلد پروژه و اجرای کامل تست‌ها با `compile_applet` و `gradle :app:testDebugUnitTest`.
