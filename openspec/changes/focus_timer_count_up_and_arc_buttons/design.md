# Design Specification: Count-Up Logic, Color Dynamics, and Arc Layout

## 1. Architectural Overview & State Flow
- **State Changes in `FocusTimerScreen`:**
  - `elapsedSeconds` (StateFlow/mutableIntStateOf): ذخیره تعداد ثانیه‌های سپری‌شده از ابتدای بازه (شروع از 0).
  - `targetSeconds`: مدت زمان هدف دوره جاری (مثلاً `focusMins * 60`).
  - `isOvertime`: وضعیت بولی مشتق‌شده (`derivedStateOf { elapsedSeconds >= targetSeconds }`).
  - `progress`: مقدار شناور `min(1f, elapsedSeconds.toFloat() / targetSeconds.toFloat())`.
  - **رنگ نوار پیشرفت (Ring / Progression Color):**
    - در صورتی که `isOvertime` برقرار باشد: گرادیان یا رنگ سبز زنده (`Color(0xFF10B981)` و `Color(0xFF059669)`).
    - در غیر این صورت: رنگ تم استاندارد بنفش/نیلی (`Color(0xFF8B5CF6)` و `Color(0xFF6366F1)`).

## 2. Timer Loop Refactoring
- به جای کم کردن از `remainingSeconds`، هر ثانیه یک واحد به `elapsedSeconds` اضافه می‌شود:
  ```kotlin
  if (isRunning) {
      delay(1000L)
      elapsedSeconds++
  }
  ```
- با عبور از `targetSeconds`، زنگ یا لرزش هشدار اولیه زده می‌شود اما تایمر متوقف نمی‌شود تا کاربر بتواند در صورت تمایل مطالعه بیشتر را ثبت کند.

## 3. UI Controls & Crescent Arc Design
- **هندسه دکمه‌ها:**
  - همه دکمه‌ها به فرم دایره‌ای (`CircleShape`) با سایز یکنواخت (۵۰ تا ۵۶ دی‌پی) تبدیل می‌شوند.
  - دکمه شروع/توقف: دایره با ابعاد ۵۶dp و رنگ پس‌زمینه آبی روشن (`Color(0xFF38BDF8)` / `Color(0xFFE0F2FE)`) یا تم هماهنگ.
  - دکمه رد کردن (Skip): دایره با رنگ آبی روشن (`Color(0xFFE0F2FE)` / آیکون آبی `Color(0xFF0284C7)`).
  - دکمه بازنشانی (Reset) و انتخاب صوت (Sound): دایره‌های شکیل با سایز ۵۰dp و استروک ملایم.
- **انحنای هلالی (Arc Offset Curve):**
  - با استفاده از فرمول سهمی یا تنظیم مقادیر `offset(y = ...)`, دکمه‌های کناری بالاتر (مثلاً `y = -10.dp`) و دکمه‌های مرکزی کمی پایین‌تر (`y = 6.dp`) یا برعکس چیده می‌شوند تا قوس هلالی ملایم و چشم‌نوازی با فواصل مساوی در راستای RTL ایجاد شود.

## 4. RTL & Persian Localization
- پشتیبانی کامل از چینش راست‌به‌چپ (`LayoutDirection.Rtl`).
- نمایش اعداد زمان به فرمت فارسی از طریق تبدیل ارقام یا کامپوننت `TimeTicker`.
