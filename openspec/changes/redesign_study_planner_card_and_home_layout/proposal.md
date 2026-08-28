# Proposal: Redesign Smart Study Plan Card & Home Dashboard Polish

## 1. Problem Statement & User Intent
The user provided a visual reference image for the tall **Smart Study Plan Card (کارت برنامه‌ریز هوشمند شتاب)** and requested specific UI/UX updates:
1. **Tall Study Planner Card Redesign (مطابق تصویر پیوست)**:
   - Modern vibrant purple gradient container with smooth rounded corners (24.dp).
   - Crisp white RTL typography for the title ("برنامه ریز\nهوشمند شتاب") and subtitle ("برنامه‌ریزی هوشمند\nو پیشرفت سؤال").
   - 3D-styled target / bullseye illustration with dart and platform pedestal.
   - Interactive white pill action button at the bottom-end: "شروع کنید" with left arrow icon.
2. **Text Refinements**:
   - Card title: "لایـک‌های رقابتی فعال" ➡️ "لیگ‌های رقابتی".
   - Performance Chart title: "نمای کلی عملکرد" ➡️ "نمای کلی".
3. **Layout & Positioning**:
   - Move the Performance Chart higher up (reduce top spacing from profile header).

---

## 2. Acceptance Criteria
1. The tall card matches the visual design in the provided image (rich purple gradient, 3D target visual, white pill "شروع کنید" button, clean typography).
2. The league card displays "لیگ‌های رقابتی".
3. The chart title displays "نمای کلی".
4. Spacing above the chart is reduced, elevating its vertical placement.
5. All Compose tests in `HomeScreenTest.kt` pass without regressions.
