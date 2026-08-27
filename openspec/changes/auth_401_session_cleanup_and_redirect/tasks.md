# Implementation Tasks: Auth 401 Session Cleanup & Auto-Redirect

- [x] **1. Data & Session Layer**
    - [x] Update `TokenManager.kt` to include comprehensive `clearAllData()` method covering all user keys, credentials, academic grade/major, and profile metadata.
    - [x] Create `SessionManager.kt` (or integrate into `TokenManager` / `ApiClient`) providing reactive `authEvents: SharedFlow<AuthEvent>`, handling local `AppDatabase.clearAllTables()`, cookie jar flushing, and cache resets.

- [x] **2. Network & Interceptors Layer**
    - [x] Connect `TokenAuthenticator.kt` failure callback to trigger `SessionManager.handleUnauthorized()`.
    - [x] Update `ResponseInterceptor.kt` to delegate HTTP 401 unhandled events to `SessionManager.handleUnauthorized()`.
    - [x] Add debounce/atomic flag to prevent multiple concurrent 401 triggers.

- [x] **3. UI & Navigation Layer**
    - [x] Add string resource `error_session_expired` in `res/values/strings.xml`.
    - [x] In `MainActivity.kt` / `ShetabNavGraph.kt`, listen to `SessionManager.authEvents`.
    - [x] Upon `AuthEvent.SessionExpired`, clear navigation backstack (`popUpTo(0) { inclusive = true }`), navigate to `Screen.Login.route`, and display the Persian session expired snackbar/toast.

- [x] **4. Verification & Testing**
    - [x] Add unit tests for `SessionManagerTest` and `TokenManagerTest` verifying data purge.
    - [x] Add Robolectric test verifying 401 response triggers logout event and database clearance.
    - [x] Compile and verify via `compile_applet`.
