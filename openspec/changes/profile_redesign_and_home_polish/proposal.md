# Proposal: Profile UI Redesign from Mockup & Home Dashboard Polish

## 1. Problem Statement & User Intent
The user requested three critical updates:
1. **Home Feature Cards Subtitles (بازگرداندن متن‌های زیر عنوان با Justify و Wrap)**:
   - Restore the descriptive subtitle texts under the titles in:
     - **لیگ‌های رقابتی** ("تو یک قدم تا جایزه")
     - **گروه‌های مطالعاتی من** ("با هم بهتر میتونیم")
     - **آزمون‌ساز** ("آزمون بساز و تمرین کن")
     - **پرسش از همکلاسی‌ها** ("سوالت رو سریع پاسخ بگیر")
   - Ensure the subtitles have appropriate max width/wrapping (`fillMaxWidth(0.58f)` or column constraints) and `TextAlign.Justify` / clean word wrap so they do not overlap with the card illustration vectors on the left.
2. **Smart Planner Image & Height Adjustments (تنظیم تصویر و ارتفاع کارت)**:
   - Move the 3D target and dart image slightly more to the left (`Alignment.CenterStart` / offset to the left) inside `FeatureCardSmartPlan`.
   - Slightly decrease the height of the top feature grid container (e.g., from `226.dp` to `212.dp` - `216.dp`).
3. **Profile Screen Redesign based on Mockup (طراحی مجدد صفحه پروفایل مطابق تصویر)**:
   - Accurately redesign `ProfileScreen.kt` according to the provided UI mockup `profile.png`:
     - **Top App Bar**: Bell notification icon on the top-left (in a soft circular container with notification dot), small online avatar on top-right.
     - **Hero User Card**:
       - Soft purple/lilac background card with generous rounded corners (`28.dp`).
       - Right side: Large circular user avatar with a white camera edit badge (`ic_camera`).
       - Left side: Full Name (e.g. "علی محمدی") with an edit pencil icon, Academic Grade (e.g. "پایه دوازدهم" with graduation cap icon `School`), Field of Study (e.g. "رشته تجربی" with book icon `MenuBook`).
     - **Personal Information Section ("اطلاعات شخصی")**:
       - Section header with user icon.
       - White rounded card with 3 clean rows divided by subtle dividers:
         1. نام و نام خانوادگی -> Value (e.g. "علی محمدی") + chevron left.
         2. پایه تحصیلی -> Value (e.g. "دوازدهم") + chevron left.
         3. رشته تحصیلی -> Value (e.g. "تجربی") + chevron left.
       - Explanatory caption: "برای ویرایش اطلاعات روی هر مورد کلیک کنید."
     - **User Account Section ("حساب کاربری")**:
       - Section header with manage account icon.
       - 4 distinct elevated cards with rounded corners, action labels, subtitles, chevron arrow, and themed right-side icons:
         1. **ارتقاء به اکانت پرو**: Subtitle "از تمام امکانات ویژه استفاده کنید", Crown/Premium icon in soft purple square.
         2. **تیکت پشتیبانی**: Subtitle "سوال یا مشکلی دارید؟ با ما در ارتباط باشید", Headset/Support icon in soft purple square.
         3. **درباره برنامه**: Subtitle "نسخه برنامه و اطلاعات بیشتر", Info icon in soft purple square.
         4. **خروج از حساب کاربری**: Subtitle "از حساب کاربری خود خارج شوید", Logout icon in soft red/pink square, red title text.

---

## 2. Acceptance Criteria
1. Subtitle texts restored in all 4 cards with proper text wrapping and no vector clipping.
2. Smart Planner image shifted slightly to the left and card row height decreased to ~214.dp.
3. Profile Screen matches the exact layout, color hierarchy, typography, and iconography shown in `profile.png`.
4. Automated UI tests pass and build succeeds.
