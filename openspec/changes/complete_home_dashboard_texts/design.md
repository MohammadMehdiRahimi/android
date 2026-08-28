# Design: Complete Home Dashboard Feature Texts

## 1. Architecture & Component Mapping
تغییرات محدود به لایه Presentation و فایل منابع `res/values/strings.xml` می‌باشد:
- **Presentation (`ui/main/`):**
  - کامپوننت `ReferenceHomeDashboard.kt` -> کارت‌های `FeatureCardStudyGroup` و `FeatureCardPeerTrouble` در گرید فیچرهای صفحه اصلی (`HomeFeatureGrid`).
- **Resource Values (`res/values/`):**
  - `strings.xml`: تعریف کلید‌های اختصاصی:
    - `home_study_groups_title`: «گروه‌های مطالعاتی»
    - `home_study_groups_subtitle`: «مطالعه گروهی و رقابت با دوستان»
    - `home_peer_trouble_title`: «پرسش از همکلاسی‌ها»
    - `home_peer_trouble_subtitle`: «پاسخ سریع به سؤالات و رفع اشکال درسی»

## 2. UI & Typography Rules
- **Font Family:** `IranSansFontFamily`
- **Line Heights & Text Overflow:** استفاده از `maxLines = 2` با تنظیم فاصله خطی مناسب (`lineHeight = 13.sp`) تا متن در تمام ابعاد و اندازه‌های صفحه نمایش بدون truncation یا ناهماهنگی بصری خوانده شود.
- **RTL Support:** چیدمان راست‌به‌چپ با استفاده از `Alignment.TopStart` و `Alignment.Start`، بدون مقادیر هاردکد `left`/`right`.
- **Colors:** استفاده از رنگ متنی استاندارد تم داشبورد (`HomeNavy` برای عناوین و `HomeMuted` برای توضیحات).

## 3. Data Flow
- این کامپوننت‌ها به عنوان اکشن‌های ناوبری صفحه اصلی به مسیرهای `my_group` (گروه‌های مطالعاتی) و `peer_trouble` (پرسش از همکلاسی‌ها / رفع اشکال) عمل می‌کنند و وضعیت داده‌ای آن‌ها استاتیک / ارائه‌محور است.
