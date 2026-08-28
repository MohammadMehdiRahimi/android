# Proposal: Exam Wizard Step 2 Persian UI Refinements & Bottom Navigation Adjustments

## 1. Overview
This change refines the second step of the Exam Creation Wizard ("تنظیم سوالات" / Question Settings) and optimizes the wizard's action buttons and navigation in accordance with the Persian Design System and mobile screen ergonomical guidelines.

## 2. Motivation & User Pain Points
1. **Top Summary Representation**: Currently, Step 2 displays exam attributes (type, grade, field, books) wrapped in boxed card surfaces. The user requested a non-card, inline text-based summary with clean Persian design typography: `نوع آزمون: تستی  |  پایه: دهم  |  رشته: ریاضی`.
2. **Step 2 RTL Alignment & Book Card Polish**: Step 2 requires strict native Right-to-Left (RTL) alignment, ensuring proper reading order for all child components, book details, topic badges, and difficulty steppers. Book cards need aesthetic improvements following Persian Design System principles (clean borders, balanced spacing, high-contrast typography, and refined badges).
3. **Cancel & Back Action Visibility**:
   - The "انصراف" (Cancel) button currently uses a subtle text-button or plain style; it should have a prominent red background (`#EF4444` / `#DC2626`) with crisp white text.
   - The wizard action buttons (ادامه، بازگشت، انصراف) collide with the system navigation bar / gesture bar at the bottom of the phone screen. They must be lifted with proper system insets and bottom padding (`navigationBarsPadding()` / additional bottom clearance).

## 3. Acceptance Criteria
1. **Text-Based Summary**:
   - Step 2 top summary replaces the card layout with a clean, horizontal text flow: `نوع آزمون: [تستی]   پایه: [دهم]   رشته: [ریاضی]` using Persian typography and subtle dot/bullet or divider accents.
2. **Strict RTL & Persian Design System for Book Cards**:
   - All elements in Step 2 adhere to RTL reading hierarchy (`Right -> Left`).
   - Book cards feature refined Persian typography, styled topic tags, distinct difficulty steppers (+/- counters), and properly aligned delete triggers.
3. **Button Styling & Elevation**:
   - The "انصراف" button is styled with a solid red background and white text.
   - The footer container adds `navigationBarsPadding()` and elevated bottom spacing so buttons never overlap Android navigation gestures or on-screen keys.
4. **Testing & Stability**:
   - Existing wizard unit/Robolectric tests pass and new UI assertions are added to verify the updated layout.
