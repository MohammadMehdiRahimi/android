# Design: Login Feature

## 1. Architecture & Data Flow (Clean Architecture)
- **UI Layer:** 
  - `LoginScreen.kt`: Jetpack Compose layout for the screen.
  - `LoginViewModel.kt`: Manages the state of the input field (mobile number), loading states, and form validation.
  - `LoginUiState.kt`: Data class holding `mobileNumber`, `isLoading`, `isMobileNumberValid`, and `errorMessage`.
- **Domain/Data Layer:**
  - `AuthRepository`: Interface for authentication actions (prepared for `requestOtp(mobileNumber: String)`).

## 2. UI Components & RTL Rules
- **Typography:** Using the app's predefined M3 typography with Vazirmatn/IranSans fonts.
- **Layout Direction:** Handled natively by the app's RTL `CompositionLocalProvider`. Using `start` and `end` for padding/alignment (e.g., `padding(start = 16.dp)`).
- **Illustration:** The `send_otp_vector` drawable is used as the central hero image.
- **Input Field:** Custom styled `BasicTextField` or `OutlinedTextField` with rounded corners (e.g., 16dp). Custom leading icon area containing the flag, text "+98", and a vertical divider. Placeholder and input text use Persian numerals or RTL alignment.
- **Submit Button:** Full-width rounded button with primary brand color, containing text "ورود و دریافت کد تأیید" and an `AutoMirrored` left-pointing arrow icon (`Icons.AutoMirrored.Filled.ArrowForward` in RTL acts as an arrow pointing left).
- **Terms Text:** `AnnotatedString` with `ClickableText` or `LinkAnnotation` to style "شرایط و قوانین" with the primary color.

## 3. Navigation Strategy
- **Route:** `login`
- **Transition from Onboarding:** When the user finishes onboarding, navigation to login will use:
  ```kotlin
  navController.navigate("login") {
      popUpTo("onboarding") { inclusive = true }
  }
  ```
  This ensures the backstack is cleared of the onboarding screen.

## 4. State Management (UDF)
- `LoginViewModel` exposes a single `StateFlow<LoginUiState>`.
- UI events: `OnMobileNumberChanged(String)`, `OnSubmitClicked`.
