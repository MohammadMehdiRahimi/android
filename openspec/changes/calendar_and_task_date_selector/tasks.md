# Actionable Tasks: Tasks Calendar Grid, New Task Date Selector & Profile Arrows

- [x] **1. Domain Date Utilities Enhancement (`JalaliDate.kt`, `DateTransformer.kt`)**
  - [x] Add month grid generator calculating the day-of-week index (0 for Saturday through 6 for Friday) for the 1st day of any given Jalali month.
  - [x] Calculate total days in month (31 for months 1-6, 30 for 7-11, 29/30 for 12).
  - [x] Add matrix generation with previous and next month filler days and formatting helper.

- [x] **2. Rich Monthly Jalali Calendar Dialog (`StudyPlanComponents.kt`)**
  - [x] Create `JalaliMonthCalendarDialog` composable styled with rounded card, M3 elevation, and smooth RTL alignment matching the reference design.
  - [x] Implement Month/Year switcher row with `<` and `>` navigation arrows and month/year title.
  - [x] Implement 7-column weekday headers (شنبه، یکشنبه، دوشنبه، سه‌شنبه، چهارشنبه، پنجشنبه، جمعه).
  - [x] Implement 7-column grid of days with interactive day cells and visual highlights for selected day, today, and adjacent months.
  - [x] Provide selected date display bar and confirmation/cancellation buttons.
  - [x] Update `StudyPlanCalendarHeader` to launch this rich monthly calendar dialog on date title click.

- [x] **3. New Task Creation Date Selector (`CreateStudyPlanViewModel.kt`, `CreateStudyPlanScreen.kt`)**
  - [x] Add `selectedDate: JalaliDate` to `CreateStudyPlanUiState`.
  - [x] Implement `selectDate(date: JalaliDate)` in `CreateStudyPlanViewModel`.
  - [x] Use `selectedDate.toGregorian().toString()` as `scheduledOn` in `CreateManualStudyTaskDto` call.
  - [x] Create `CreatePlanDateSelectorSection` composable at the very top of `CreateStudyPlanScreen.kt` featuring quick pills (امروز, فردا, تقویم/تاریخ دلخواه) and active date display.
  - [x] Integrate monthly calendar dialog with the custom date button.

- [x] **4. Profile Screen Arrow Direction Verification (`ProfileScreen.kt`)**
  - [x] Updated all chevrons in `PersonalInfoRow` and `AccountActionCard` to `Icons.Default.KeyboardArrowLeft` so they strictly point to the left in RTL layout.

- [x] **5. Verification & Testing**
  - [x] Verified complete app compilation successfully with `compile_applet`.
