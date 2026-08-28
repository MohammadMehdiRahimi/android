# Tasks Checklist: Centralize Backend Base URL in `constant.kt`

- [x] **1. Constant Definition (Root Package)**
  - [x] Create `app/src/main/java/com/example/constant.kt`.
  - [x] Declare `const val API_BASE_URL = "https://api.weshetab.ir"`.

- [x] **2. Network Layer Integration**
  - [x] Update `ApiClient.kt` to use `API_BASE_URL` from `com.example.constant.kt`.
  - [x] Audit `TokenAuthenticator.kt`, `ApiService.kt`, and related network classes to ensure no hardcoded URLs remain.

- [x] **3. Verification & Testing**
  - [x] Run `gradle :app:testDebugUnitTest` to ensure all unit tests and network mocks pass.
  - [x] Run `compile_applet` to verify build integrity.

