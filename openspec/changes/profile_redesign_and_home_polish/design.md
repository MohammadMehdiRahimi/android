# Technical Design: Profile Redesign & Home Dashboard Polish

## 1. Home Dashboard Updates (`ReferenceHomeDashboard.kt`)

### 1.1 Top Grid Height & Smart Planner Image
- Reduce the Top Grid `Row` height from `226.dp` to `214.dp`.
- In `FeatureCardSmartPlan`:
  - Position the 3D illustration with `Alignment.Center` and `offset(x = 18.dp, y = 4.dp)` (shifting it toward the left in RTL visual coordinates).

### 1.2 Subtitles Restoration with Justify & Wrap
- Restore subtitle texts in:
  - `FeatureCardLeague`: Add `Text("تو یک قدم تا جایزه", modifier = Modifier.fillMaxWidth(0.62f), textAlign = TextAlign.Justify, ...)`
  - `FeatureCardStudyGroup`: Add `Text("با هم بهتر میتونیم", modifier = Modifier.fillMaxWidth(0.62f), textAlign = TextAlign.Justify, ...)`
  - `FeatureCardPeerTrouble`: Add `Text("سوالت رو سریع پاسخ بگیر", modifier = Modifier.fillMaxWidth(0.62f), textAlign = TextAlign.Justify, ...)`
  - `FeatureCardExamBuilder`: Add `Text("آزمون بساز و تمرین کن", modifier = Modifier.fillMaxWidth(0.62f), textAlign = TextAlign.Justify, ...)`

---

## 2. Profile Screen Redesign (`ProfileScreen.kt`)

### 2.1 Layout Architecture (Persian RTL)
1. **Top Bar**:
   - Left: Notification bell icon in a soft elevated rounded box with a purple notification indicator dot.
   - Right: Small circular avatar thumbnail with an active green online badge.
2. **Hero Header Card**:
   - Background: `Color(0xFFF7F5FE)` or `Color(0xFFF3F0FC)` with `RoundedCornerShape(26.dp)`.
   - Right: Large 94dp circular user avatar with border + camera edit icon button badge (`Alignment.BottomStart` or `BottomEnd`).
   - Left:
     - Full Name + edit pencil icon (`Icons.Default.Edit` in `Color(0xFF7543EA)`).
     - Row with `Icons.Default.School` (or vector) + Grade (e.g., "پایه دوازدهم").
     - Row with `Icons.Default.MenuBook` (or `AutoStories`) + Field of Study (e.g., "رشته تجربی").
3. **Personal Info Section ("اطلاعات شخصی")**:
   - Title header with `Icons.Outlined.Person` in purple (`#7543EA`).
   - Unified white card (`RoundedCornerShape(20.dp)`) with 3 rows separated by subtle dividers:
     - Row 1: "نام و نام خانوادگی" (Right) -> Full Name (Left) + `KeyboardArrowLeft`
     - Row 2: "پایه تحصیلی" (Right) -> Grade text (Left) + `KeyboardArrowLeft`
     - Row 3: "رشته تحصیلی" (Right) -> Field text (Left) + `KeyboardArrowLeft`
   - Bottom note: "برای ویرایش اطلاعات روی هر مورد کلیک کنید." (`fontSize = 11.5.sp`, `color = Color.Gray`).
4. **User Account Section ("حساب کاربری")**:
   - Title header with `Icons.Outlined.ManageAccounts` in purple.
   - 4 Action Cards (`RoundedCornerShape(18.dp)`, white card, subtle shadow):
     - **ارتقاء به اکانت پرو**: Purple icon box with `Crown` / `WorkspacePremium`, title, subtitle "از تمام امکانات ویژه استفاده کنید", chevron.
     - **تیکت پشتیبانی**: Purple icon box with `Headphones` / `SupportAgent`, title, subtitle "سوال یا مشکلی دارید؟ با ما در ارتباط باشید", chevron.
     - **درباره برنامه**: Purple icon box with `Info`, title, subtitle "نسخه برنامه و اطلاعات بیشتر", chevron.
     - **خروج از حساب کاربری**: Light red icon box with `ExitToApp`, title in `Color(0xFFE53935)`, subtitle "از حساب کاربری خود خارج شوید", chevron.
5. **State & Interaction Handling**:
   - Dialog for updating / editing personal information (Name, Grade, Field).
   - Avatar image picker and cropper (`AvatarCropDialog`).
   - Logout dialog / handler invoking `onLoggedOut()`.
   - Pro upgrade handler invoking `onUpgradeClick()`.

---

## 3. Testing Matrix
- Verify that `HomeScreenTest.kt` passes.
- Verify `ProfileScreen` renders smoothly with all items and Persian typography.
