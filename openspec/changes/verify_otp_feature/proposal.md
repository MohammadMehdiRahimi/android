# Proposal: Verify OTP Feature (صفحه تأیید کد)

## 1. Feature Overview
The Verify OTP feature enables users to enter and verify the 6-digit One-Time Password (OTP) received on their mobile device after initiating login. This step validates ownership of the mobile number, persists authentication credentials upon success, and directs the user to the main application dashboard (or profile setup).

## 2. Acceptance Criteria
- **Pixel-Perfect UI:** Exact visual fidelity to the provided design mockups with soft ambient background gradient (`#FCFBFF` to `#F6F4FE`), proper typography, rounded shapes (18-20dp), and brand primary purple colors (`#6851FF` / `#5E3CEE`).
- **RTL & Persian Localization:** Native Right-to-Left (RTL) layout using `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl)`. All texts externalized to `res/values/strings.xml` and styled with Persian typography (`VazirmatnFontFamily`). All numeric values (timer, countdown, phone numbers, OTP digits) converted and rendered using Persian digits (`۰-۹`).
- **Hero Graphic & Resource Management:** Transfer `/verify-otp-vector.png` from the root workspace to `/app/src/main/res/drawable/verify_otp_vector.png`, integrate it seamlessly with high-density vector rendering, and remove the root artifact.
- **Navigation Flow & Argument Passing:**
  - Placed directly in the navigation sequence after `login_phone` (`login_otp` or `verify_otp/{phoneNumber}`).
  - Navigated from `LoginScreen` upon successful OTP request, receiving the normalized mobile number as a type-safe/string navigation argument.
  - Back navigation from the top-bar back icon returns smoothly to `LoginScreen` allowing phone number correction.
- **Interactive 6-Digit OTP Field:**
  - 6 individual rounded input cells (50dp x 56dp) with state indicators (inactive border `#E2E8F0`, active focused border `#6851FF` with subtle glowing stroke/cursor, error border `#EF4444`).
  - Seamless auto-focus to next digit cell upon entry, backward deletion on backspace key, and support for pasting 6-digit codes (including Persian/Arabic numerals).
  - Optimized to eliminate unnecessary recompositions (using `remember`, stable state lambdas, and decoupled text state).
- **Countdown Timer & Resend Mechanism:**
  - 2-minute (120-second) countdown timer running asynchronously with `LaunchedEffect` and `delay(1000L)`.
  - Timer row showing a clock icon, "زمان باقی‌مانده:" label, and Persian-formatted time (e.g. "۰۱:۵۹").
  - Pill-shaped "ارسال مجدد کد" button showing remaining minutes/seconds (e.g. "(۲ دقیقه بعد)") when disabled, becoming active and clickable once the countdown reaches zero. Clicking resend triggers API request and resets the countdown.
- **Verification Action & State Handling:**
  - Bottom action button "تأیید کد" with RTL-mirrored forward arrow icon and loading indicator during verification.
  - Disabled or active based on whether all 6 digits have been entered.
  - On verification success, persists auth session and navigates to the app dashboard (`popUpTo("login_phone") { inclusive = true }`).
  - On verification failure, displays clear inline error message in Persian and highlights input cells in error state.
- **Quality & Testing:**
  - 100% testable architecture with test tags (`Modifier.testTag`) on all interactive and semantic elements.
  - Unit tests verifying ViewModel state transitions, timer countdown logic, code validation, and resend triggers.
  - Robolectric Compose UI tests validating rendering, OTP typing interaction, and button state toggling.

## 3. Out of Scope
- SMS Auto-read / SMS Retriever API (can be added in a future enhancement).
- Biometric authentication / Passkey enrollment.
