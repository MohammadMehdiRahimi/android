# Design: Exam Entry Screen UI Redesign

## 1. UI Structure & Components
The screen will be structured in Jetpack Compose to precisely match the uploaded design:

1. **Scaffold / Background:**
   - Soft neutral light background (`#F8F9FD` / `#FAFAFC`).
   - Vertical scrolling column with generous spacing (16.dp to 20.dp).

2. **App Bar / Header:**
   - Circular Back button with light border and elevation.
   - Centered or top aligned title: `Text("ورود به آزمون")` with `ic_clipboard_check` icon tint `#5B42F3`.
   - Subtitle: `Text("با وارد کردن شناسه آزمون، وارد آزمون شوید")` in muted grey color (`#64748B`).

3. **Section 1: Exam ID Container (`شناسه آزمون`):**
   - Header with bullet dot: `Box(modifier = Modifier.size(6.dp).background(Color(0xFF5B42F3), CircleShape))` + `Text("شناسه آزمون")`.
   - Container card: White rounded card with soft border `#E2E8F0` or `#EDE9FE`.
   - Inner row:
     - "ID" tag: rounded rect with background `#EDE9FE` and text `#5B42F3`.
     - Value text / TextField with bold Persian typography.
     - Scanner icon: `IconButton` with rounded square `#5B42F3` background and white QR/Scanner icon.
   - Helper text beneath: `Text("شناسه آزمون را از مدرس یا برگزارکننده دریافت کنید.")` with small font size (11.sp).

4. **Section 2: Specifications Card (`مشخصات آزمون`):**
   - Header with bullet dot: `• مشخصات آزمون`.
   - Card container with white background and rounded corners (24.dp).
   - Top banner:
     - Vector / illustration of clipboard with checkmarks and clock timer.
     - Large bold title: e.g. "آزمون زیست شناسی دهم".
     - Subtitle chip badge: "آزمون جامع فصل ۱ تا ۳" with light purple background `#F3F0FF` and text `#5B42F3`.
   - List of 7 specification rows with `HorizontalDivider`:
     1. Organizer: استاد احمدی (Person icon)
     2. Exam Date: جمعه ۲۴ خرداد ۱۴۰۳ (Calendar icon)
     3. Start Time: ۱۰:۰۰ صبح (Clock icon)
     4. Duration: ۹۰ دقیقه (Hourglass icon)
     5. Questions Count: ۴۰ سوال (FormatListNumbered / Checklist icon)
     6. Question Type: چهارگزینه‌ای (HelpOutline / Quiz icon)
     7. Total Score: ۴۰ نمره (StarOutline icon)
   - Each row has:
     - Right: Rounded square box (36.dp) with light purple background `#F3F0FF` and purple icon `#5B42F3`.
     - Left: Label on top ("برگزارکننده") and Value below ("استاد احمدی").

5. **Warning Note Card (`توجه داشته باشید`):**
   - Rounded container (16.dp) with light purple/blue tint (`#F5F3FF`).
   - Info icon on start/end, Title: "توجه داشته باشید" in bold `#5B42F3`.
   - Text: "پس از شروع آزمون، امکان خروج وجود ندارد و زمان آزمون شروع خواهد شد.".

6. **Bottom Action Button:**
   - Prominent sticky or bottom-padded button with purple color `#5B42F3`.
   - Text: "ورود به آزمون" with forward / enter icon (`Icons.AutoMirrored.Filled.Login` / `ExitToApp`).
   - Navigates to `exam_taking`.

## 2. RTL & Theming
- Native RTL support with `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl)`.
- `IranSansFontFamily` typography.
- Semantic test tags on all interactive elements (`exam_details_screen`, `exam_id_input_card`, `enter_exam_button`, etc.).
