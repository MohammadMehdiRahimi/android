# Tasks Checklist: Mock Data & Offline Mode

- [x] **1. Mock Network Interceptor Implementation**
  - [x] Create `MockDataInterceptor.kt` in `com.example.network` providing simulated responses for `/auth/*`, `/base-info/*`, `/users/me`, `/progress/dashboard`, `/study-plans/*`, `/study-groups/*`.
  - [x] Integrate `MockDataInterceptor` into `ApiClient.kt` OkHttpClient builder.

- [x] **2. Auth & Onboarding Flow Seamlessness**
  - [x] Ensure entering any phone and OTP immediately creates an authenticated session with complete user profile.
  - [x] Ensure base info screen loads pre-populated grades and fields.

- [x] **3. Home Performance Chart & Study Plan Data**
  - [x] Populate mock data for weekly/monthly performance chart buckets.
  - [x] Populate mock data for daily study tasks and study plan schedule.

- [x] **4. Verification & Testing**
  - [x] Run `compile_applet` to ensure successful compilation.
  - [x] Run unit tests to ensure compatibility.
