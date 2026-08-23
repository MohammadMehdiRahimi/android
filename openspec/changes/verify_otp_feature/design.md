# Design: Verify OTP Feature (صفحه تأیید کد)

## 1. Architecture & Modularization (Feature-First Clean Architecture)
The feature is placed inside `com.example.ui.features.auth.otp` (or `verify_otp`) following MVVM and Unidirectional Data Flow (UDF).

```text
com.example.ui.features.auth.otp/
├── VerifyOtpScreen.kt      # Main Composable screen & sub-composables
├── VerifyOtpViewModel.kt   # Business logic, countdown timer, validation, auth verification
└── VerifyOtpUiState.kt     # Immutable UI State model
```

### Data Flow
1. **Navigation Entry:** `LoginScreen` calls `onNavigateToOtp(phoneNumber)`. `MainActivity` NavHost receives `phoneNumber` via route argument `verify_otp/{phoneNumber}` (or URL-encoded parameter).
2. **Initialization:** `VerifyOtpViewModel` is initialized with the phone number and starts the 120-second countdown timer.
3. **User Input:** Entering digits in the OTP cells dispatches `onOtpDigitChanged(index, char)` or `onOtpCodeChanged(fullCode)` to the ViewModel.
4. **Resend Code:** User taps "ارسال مجدد کد" when timer reaches 0, which triggers `resendOtp()`, resetting timer to 120s.
5. **Submission:** User taps "تأیید کد" or fills the 6th digit; `VerifyOtpViewModel.verifyCode()` validates against `ApiService` / `AuthRepository`, stores the auth token via `TokenManager`, and emits a navigation event to `dashboard`.

---

## 2. State Management (UDF)

### `VerifyOtpUiState`
```kotlin
data class VerifyOtpUiState(
    val phoneNumber: String = "",
    val formattedPhoneNumber: String = "",
    val otpCode: String = "", // 6 digits normalized (0-9)
    val remainingSeconds: Int = 120,
    val isTimerActive: Boolean = true,
    val isLoading: Boolean = false,
    val isResending: Boolean = false,
    val errorMessage: String? = null,
    val isCodeComplete: Boolean = false,
    val isVerificationSuccess: Boolean = false
) {
    val formattedTime: String
        get() {
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            val raw = String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
            return raw.toPersianDigits()
        }
}
```

### `VerifyOtpViewModel`
- Manages an internal `MutableStateFlow<VerifyOtpUiState>` and exposes an immutable `StateFlow<VerifyOtpUiState>`.
- **Countdown Coroutine:** Launches a coroutine in `viewModelScope` with a tick interval of 1000ms. Updates `remainingSeconds` and sets `isTimerActive = false` upon reaching 0.
- **OTP Validation:** Strips Persian/Arabic digits to standard ASCII digits. Ensures max length is 6. Toggles `isCodeComplete` when length == 6.
- **Recomposition Optimization:** OTP cells are rendered as a custom multi-box layout backed by a single hidden `BasicTextField` (or individual discrete focus nodes) to minimize recompositions across Compose frames during rapid typing.

---

## 3. UI Components & Layout Specification

### 3.1 RTL & Visual Styling
- **Layout Direction:** Natively wrapped in `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl)`.
- **Spacing:** Responsive layout built on an 8dp grid using `Modifier.fillMaxSize()`, `verticalScroll()`, and `Arrangement.SpaceBetween`.
- **Colors & Tokens:**
  - Background Gradient: `Brush.verticalGradient(listOf(Color(0xFFFCFBFF), Color(0xFFF6F4FE)))`
  - Primary Action Color: `Color(0xFF6851FF)` (Brand Violet/Purple)
  - Text Dark: `Color(0xFF191B2D)`
  - Text Gray: `Color(0xFF64748B)`
  - Box Inactive Background: `Color.White` with border `Color(0xFFE2E8F0)`
  - Box Active/Focused Border: `Color(0xFF6851FF)` with light purple tint fill `Color(0xFFF5F3FF)`
  - Error Border: `Color(0xFFEF4444)`

### 3.2 Component Breakdown (Matching UI Design Mockup)
1. **Top Bar Header:**
   - Back arrow navigation button on start edge (`Icons.AutoMirrored.Filled.ArrowBack` or chevron icon).
   - Brand Logo: "⚡ شتاب" centered or aligned with typography styling.
2. **Hero Illustration:**
   - Vector graphic `R.drawable.verify_otp_vector` (3D smartphone with purple lock bubble and passcode symbols) centered in a responsive aspect ratio box (max height 260-280dp).
3. **Title & Subtitle:**
   - Title: "کد تأیید را وارد کنید" (`fontSize = 22.sp`, `fontWeight = FontWeight.ExtraBold`, `color = TextDark`).
   - Subtitle: "کد ۶ رقمی ارسال شده به شماره" followed by the Persian formatted phone number (e.g. "۰۹۱۲ ۳۴۵ ۶۷۸۹") and "را وارد کنید.".
4. **6-Digit OTP Input Row:**
   - 6 square/rounded boxes (`width = 48.dp`, `height = 56.dp`, `shape = RoundedCornerShape(16.dp)`).
   - Displaying Persian digit with large bold typography (`22.sp`, `FontWeight.Bold`).
   - Active box displays a blinking cursor bar when focused and empty.
5. **Timer & Resend Row:**
   - Clock icon + "زمان باقی‌مانده: " + "۰۱:۵۹" (Persian numerals).
   - Resend Button: Pill-shaped button (`RoundedCornerShape(20.dp)`), containing circular refresh icon and "ارسال مجدد کد (۲ دقیقه بعد)" when disabled; becomes highlighted "ارسال مجدد کد" when countdown reaches 0.
6. **Bottom Action Button:**
   - Full-width rounded button (`RoundedCornerShape(18.dp)`, `height = 54.dp`).
   - Text: "تأیید کد" with forward arrow icon (`Icons.AutoMirrored.Filled.ArrowForward` or arrow icon pointing left in RTL).
   - Shows `CircularProgressIndicator` when `isLoading == true`.

---

## 4. Navigation Strategy
- **Route:** `verify_otp/{phoneNumber}` (aliased or replacing legacy `login_otp`).
- **Trigger from Login:**
  ```kotlin
  navController.navigate("verify_otp/${Uri.encode(phoneNumber)}")
  ```
- **Success Action:**
  ```kotlin
  navController.navigate("dashboard") {
      popUpTo("login_phone") { inclusive = true }
  }
  ```
- **Back Action:** Pops back to `login_phone` keeping the user's previously entered phone number.

---

## 5. Testability
- Semantic `Modifier.testTag` applied:
  - `otp_back_button`
  - `otp_hero_image`
  - `otp_title`
  - `otp_subtitle`
  - `otp_input_field`
  - `otp_box_0` through `otp_box_5`
  - `otp_timer_text`
  - `otp_resend_button`
  - `otp_submit_button`
  - `otp_error_text`
