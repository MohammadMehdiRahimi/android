# Tasks Checklist: Profile Redesign & Home Dashboard Polish

- [x] **1. Home Dashboard Presentation Layer (`ReferenceHomeDashboard.kt`)**
  - [x] Restore subtitles for "لیگ‌های رقابتی", "گروه‌های مطالعاتی من", "پرسش از همکلاسی‌ها", and "آزمون‌ساز" with `fillMaxWidth(0.60f)` & wrap.
  - [x] Shift Smart Planner 3D image to the left and adjust top grid height to `216.dp`.

- [x] **2. Profile Screen Redesign (`ProfileScreen.kt`)**
  - [x] Implement Top Bar with notification bell & online status avatar.
  - [x] Implement Hero Card with avatar camera badge, full name, edit icon, grade row, and field of study row.
  - [x] Implement "اطلاعات شخصی" section card with 3 rows (نام و نام خانوادگی، پایه تحصیلی، رشته تحصیلی) and bottom help text.
  - [x] Implement "حساب کاربری" section with 4 specialized action cards (ارتقاء به اکانت پرو، تیکت پشتیبانی، درباره برنامه، خروج از حساب کاربری).
  - [x] Retain real API integrations (GetMe, upload avatar, logout, persist session).

- [x] **3. Verification & Testing**
  - [x] Run `compile_applet` to confirm successful build.
  - [x] Check automated UI tests.

