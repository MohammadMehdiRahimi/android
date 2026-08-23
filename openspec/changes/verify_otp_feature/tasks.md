# Implementation Tasks: Verify OTP Feature (صفحه تأیید کد)

## 1. Preparation & Asset Migration
- [x] Move `/verify-otp-vector.png` to `/app/src/main/res/drawable/verify_otp_vector.png`.
- [x] Delete `/verify-otp-vector.png` from project root.

## 2. Resource Management (Strings & Layouts)
- [x] Add Persian string resources to `res/values/strings.xml`:
  - `otp_title`: "کد تأیید را وارد کنید"
  - `otp_subtitle_prefix`: "کد ۶ رقمی ارسال شده به شماره"
  - `otp_subtitle_suffix`: "را وارد کنید."
  - `otp_timer_label`: "زمان باقی‌مانده:"
  - `otp_resend_code`: "ارسال مجدد کد"
  - `otp_resend_cooldown`: "(۲ دقیقه بعد)"
  - `otp_submit_button`: "تأیید کد"
  - `otp_error_invalid_code`: "کد تأیید وارد شده نامعتبر یا منقضی شده است."
  - `otp_resend_success`: "کد تأیید مجدداً ارسال شد."

## 3. Presentation State & ViewModel (MVVM / UDF)
- [x] Create package `ui/features/auth/otp/` (or `verify_otp/`).
- [x] Create `VerifyOtpUiState.kt` with state fields (`phoneNumber`, `formattedPhoneNumber`, `otpCode`, `remainingSeconds`, `isTimerActive`, `isLoading`, `isResending`, `errorMessage`, `isCodeComplete`).
- [x] Create `VerifyOtpViewModel.kt`:
  - Initialize countdown timer (120 seconds) via Kotlin Coroutines.
  - Implement digit input handling and English/Persian numeral normalization.
  - Implement resend OTP logic and timer reset.
  - Implement `verifyCode()` logic (verifying OTP, saving token via `TokenManager`, handling error states).

## 4. UI Layer Implementation (Jetpack Compose)
- [x] Implement `VerifyOtpScreen.kt`:
  - Top Bar with back navigation button (`Icons.AutoMirrored.Filled.ArrowBack`) and brand header "⚡ شتاب".
  - Hero illustration displaying `R.drawable.verify_otp_vector`.
  - Title and formatted subtitle displaying the Persian-formatted phone number (e.g. `۰۹۱۲ ۳۴۵ ۶۷۸۹`).
  - 6-cell custom OTP input layout with active cursor indicator, focus management, and paste support without extra recompositions.
  - Timer and Resend pill button displaying live Persian countdown ("۰۱:۵۹").
  - Primary bottom button ("تأیید کد") with RTL-mirrored arrow and loading state.
  - Apply exact padding, colors, shadows, and test tags (`Modifier.testTag`).

## 5. Navigation Integration
- [x] Update `MainActivity.kt` NavHost to declare `verify_otp/{phoneNumber}` route and pass `phoneNumber` argument to `VerifyOtpScreen`.
- [x] Update `LoginScreen.kt` to pass the entered mobile number when navigating to the OTP screen upon request success.

## 6. Testing & Quality Assurance
- [x] Create `VerifyOtpViewModelTest.kt`:
  - Test OTP code entry, length limit, and digit normalization.
  - Test countdown timer progression and expiration.
  - Test resend OTP functionality and timer reset.
  - Test verification success and failure states.
- [x] Create `VerifyOtpScreenTest.kt` (Robolectric / Compose Test Rule):
  - Test UI layout rendering, OTP box input behavior, timer display, and button interactions in RTL mode.
- [x] Execute `compile_applet` and verify zero build/lint regressions.

