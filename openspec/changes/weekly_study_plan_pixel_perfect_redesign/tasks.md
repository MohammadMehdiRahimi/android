# Tasks: Refine Weekly Study Plan UI

- [ ] 1. **Week Selector Refinements (`PixelPerfectWeekSelector`)**
    - [ ] 1.1 Show 4 days visible per viewport with horizontal scroll (`LazyRow` with scroll state).
    - [ ] 1.2 Put Right arrow on the right side and Left arrow on the left side of the selector.
    - [ ] 1.3 Arrow buttons scroll the list by day/page.
    - [ ] 1.4 Style selected/today day with soft light purple (بنفش کمرنگ `#EDE8FF` background and `#5B2CFF` text/border).
    - [ ] 1.5 Decrease corner radius to 16dp and adjust typography.

- [ ] 2. **Session Cards Refinements (`PixelPerfectSessionCard`)**
    - [ ] 2.1 Remove text labels «ویرایش» and «حذف», displaying only clean icons.
    - [ ] 2.2 Reduce border radius to 16dp.
    - [ ] 2.3 Reduce vertical height / padding for a more compact and minimal look.
    - [ ] 2.4 Reduce font sizes (title 14.5sp, subtitle 12sp, badges 10.5sp).

- [ ] 3. **Add Session Button Refinements (`AddSessionDashedButton`)**
    - [ ] 3.1 Make the dashed dots finer (`PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)` and 1.dp stroke).
    - [ ] 3.2 Reduce height to 48dp and corner radius to 16dp.
    - [ ] 3.3 Reduce font size to 13.5sp.

- [ ] 4. **Summary Card & Header Typography & Radius (`PixelPerfectDailySummaryCard`, `PixelPerfectPlanHeader`)**
    - [ ] 4.1 Reduce corner radius of summary card to 16dp and compact height.
    - [ ] 4.2 Scale down typography across header and summary card.

- [ ] 5. **Testing & Verification**
    - [ ] 5.1 Run test suite to verify UI logic and interactions.
    - [ ] 5.2 Verify app builds cleanly via `compile_applet`.
