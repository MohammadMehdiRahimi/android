# Tasks Checklist: Centralize Backend Base URL in `constant.kt`

- [ ] **1. Constant Definition (Root Package)**
  - [ ] Create `app/src/main/java/com/example/constant.kt`.
  - [ ] Declare `const val API_BASE_URL = "https://api.weshetab.ir"`.

- [ ] **2. Network Layer Integration**
  - [ ] Update `ApiClient.kt` to use `API_BASE_URL` from `com.example.constant.kt`.
  - [ ] Audit `TokenAuthenticator.kt`, `ApiService.kt`, and related network classes to ensure no hardcoded URLs remain.

- [ ] **3. Verification & Testing**
  - [ ] Run `gradle :app:testDebugUnitTest` to ensure all unit tests and network mocks pass.
  - [ ] Run `compile_applet` to verify build integrity.
