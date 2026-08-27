# Proposal: Centralize Backend Base URL in `constant.kt`

## 1. Problem Statement & Motivation
Currently, backend network endpoints rely on inline definitions or scattered strings within network configuration files (e.g. `ApiClient.kt`, `TokenAuthenticator.kt`). To improve maintainability, enforce environment consistency, and adhere strictly to clean code architecture, the backend base URL should be centralized in a dedicated constant file at the root package level (`constant.kt`).

All network requests across the entire application must reference this single source of truth (`API_BASE_URL`).

---

## 2. Scope of Changes
- **Constant Definition**:
  - Create `/app/src/main/java/com/example/constant.kt` (or package root `com.example`).
  - Define `const val API_BASE_URL = "https://api.weshetab.ir"` (and normalized helper if required).
- **Network Configuration**:
  - Update `ApiClient.kt` to consume `API_BASE_URL` from `constant.kt`.
  - Update any interceptors, authenticators (`TokenAuthenticator.kt`), or network helpers referencing the backend URL to use `API_BASE_URL`.
- **Consistency Verification**:
  - Scan the entire codebase to ensure no other hardcoded base URL strings exist.

---

## 3. Acceptance Criteria
1. `constant.kt` exists in the root package (`com.example`) defining `const val API_BASE_URL = "https://api.weshetab.ir"`.
2. `ApiClient` and all Retrofit / OkHttp clients use `API_BASE_URL` as their base URL.
3. Refresh token / authenticator calls dynamically use `API_BASE_URL`.
4. All existing tests pass and the application compiles cleanly.
