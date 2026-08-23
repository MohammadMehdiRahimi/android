# Implementation Tasks: Login Feature

## 1. Preparation
- [x] Move `send_otp_vecotr.png` to `/app/src/main/res/drawable/send_otp_vector.png` and delete from root. (Completed)

## 2. Resources (Strings & Drawables)
- [x] Add required Persian strings to `res/values/strings.xml`:
  - `login_title`: "شماره موبایل خود را وارد کنید"
  - `login_subtitle`: "کد تأیید برای شما ارسال خواهد شد."
  - `login_phone_placeholder`: "۰۹۱۲ ۳۴۵ ۶۷۸۹"
  - `login_phone_label`: "شماره موبایل"
  - `login_submit_button`: "ورود و دریافت کد تأیید"
  - `login_terms_prefix`: "با ورود به برنامه، با "
  - `login_terms_link`: "شرایط و قوانین"
  - `login_terms_suffix`: " شتاب موافقت می‌کنید."
- [x] Create or import Iran flag drawable (`ic_flag_iran.xml`).

## 3. Presentation Layer (State & ViewModel)
- [x] Create package `ui/features/auth/login/`.
- [x] Create `LoginUiState` data class (mobileNumber, isLoading, isValid, errorMessage).
- [x] Create `LoginViewModel` with methods to handle input changes and validate the phone number (regex/length check).

## 4. Presentation Layer (UI & Compose)
- [x] Implement `LoginScreen` composable function.
- [x] Add the logo Header ("شتاب" text and lightning bolt).
- [x] Add the hero illustration (`send_otp_vector.png`).
- [x] Build the Phone Number Input Field:
  - Custom container with border and rounded corners.
  - Left section (in LTR) / Right section (in RTL): Flag + "+98" + Dropdown icon + Vertical Divider.
  - Right section (in LTR) / Left section (in RTL): Text field for number input.
- [x] Build the Submit Button with the text and left-pointing arrow icon.
- [x] Build the Terms & Conditions footer using `buildAnnotatedString` and `clickable`.
- [x] Apply pixel-perfect padding, spacing, and RTL alignment according to the provided UI image.

## 5. Navigation Integration
- [x] Add the Login screen route to `MainScreen.kt` or the main NavHost.
- [x] Update `OnboardingScreen` (or where Onboarding finishes) to navigate to the Login screen, ensuring `popUpTo` clears Onboarding from the backstack.

## 6. Testing
- [x] Write Unit Test for `LoginViewModel` (validating phone number format).
- [x] Write Compose UI tests for `LoginScreen`.
