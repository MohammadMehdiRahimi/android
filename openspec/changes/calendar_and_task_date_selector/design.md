# Feature Design: Tasks Calendar Grid, New Task Date Selector & Profile Arrows

## 1. Architectural Alignment
Following the Clean Architecture and Feature-First structure defined in `openspec/project.md`:
- **Domain Layer (`domain/date/`):**
  - Extend `JalaliDate` and `DateTransformer` to support full Month Matrix generation (determining first day of month weekday in Jalali calendar and days per month).
- **Presentation Layer (`ui/features/studyplan/` & `ui/features/profile/`):**
  - **`JalaliMonthCalendarDialog`**: Custom Jetpack Compose Material 3 dialog with monthly grid layout (7 columns for Saturday -> Friday), Month/Year navigators, Today indicator, and selection state.
  - **`CreateStudyPlanDateSection`**: Top component in `CreateStudyPlanScreen` displaying quick chips ("امروز", "فردا", "تاریخ دلخواه") and the formatted Persian date banner.
  - **`CreateStudyPlanViewModel` & `CreateStudyPlanUiState`**: Add `selectedScheduledDate: JalaliDate` to state and pass the corresponding ISO date to `CreateManualStudyTaskDto`.
  - **`ProfileScreen.kt`**: Verify left arrow icons for all profile action rows.

## 2. RTL UI & Typography Rules
- Use `LocalLayoutDirection provides LayoutDirection.Rtl`.
- Iranian calendar week starts on Saturday (شنبه: `ش`, یکشنبه: `ی`, دوشنبه: `د`, سه‌شنبه: `س`, چهارشنبه: `چ`, پنج‌شنبه: `پ`, جمعه: `ج`).
- All numeric day labels are formatted with `toPersianNumber()`.
- Persian font `IranSansFontFamily` applied consistently.

## 3. Data Flow & State Management
- When opening `JalaliMonthCalendarDialog`:
  - Current month/year is initialized from `selectedJalaliDate`.
  - Clicking on a day invokes `onDateSelected(date)` and dismisses the dialog.
- In `CreateStudyPlanScreen`:
  - `selectedScheduledDate` default is today (`DateTransformer.getTodayJalali()`).
  - Selecting another date updates the UI state.
  - Submitting sends `scheduledOn = selectedScheduledDate.toGregorian().toString()`.
