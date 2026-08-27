# Technical Design: Auth 401 Session Cleanup & Auto-Redirect

## 1. Architectural Overview & Data Flow
The 401 unauthorized handling follows a clean, reactive event-driven architecture using Unidirectional Data Flow (UDF).

```text
[OkHttp / Retrofit API Request]
            │
            ▼ (HTTP 401 Response)
   [TokenAuthenticator]
       ├──> Tries /auth/refresh
       └──> Fails / Token Invalid
            │
            ▼
[SessionManager / AuthEventManager]
  ├── 1. Dispatches onUnauthorized() / clearSession()
  │     ├── TokenManager.clearAllData() (Tokens, Profile, Expiry)
  │     ├── PersistentCookieJar.clear()
  │     ├── AppDatabase.clearAllTables() (IO Coroutine)
  │     └── SharedPreferences Cache Invalidation
  │
  └── 2. Emits AuthEvent.SessionExpired to SharedFlow
            │
            ▼ (Collected via LaunchedEffect in MainActivity / MainScaffold)
[Navigation Controller]
  └──> navController.navigate(Screen.Login.route) {
         popUpTo(0) { inclusive = true }
         launchSingleTop = true
       }
```

---

## 2. Core Components

### 2.1. `SessionManager` / `AuthEventManager`
A centralized singleton or component injected across network and UI layers:
*   Exposes `val authEvents: SharedFlow<AuthEvent>` with `replay = 0` and `extraBufferCapacity = 1`.
*   Provides `fun handleUnauthorized(reason: String? = null)` which:
    *   Triggers non-blocking clearing of local Room database (`AppDatabase.clearAllTables()` on `Dispatchers.IO`).
    *   Calls `tokenManager.clearAllData()`.
    *   Calls `cookieJar.clear()`.
    *   Clears catalog preferences.
    *   Emits `AuthEvent.SessionExpired`.

### 2.2. Network Layer Interception
1.  **`TokenAuthenticator`:**
    *   When `/auth/refresh` responds with non-200 or 401, or token is invalid, calls `sessionManager.handleUnauthorized()`.
2.  **`ResponseInterceptor`:**
    *   For unauthenticated responses (401) on non-refresh routes where authenticator cannot resolve, guarantees `sessionManager.handleUnauthorized()` is invoked once (with debounce to avoid duplicate storms).

### 2.3. Data & Storage Purge
*   **`TokenManager.clearAllData()`:** Clears `jwt_token`, `refresh_token`, `registration_token`, `user_id`, `user_phone`, `user_role`, `user_full_name`, `user_title`, `profile_image_url`, `global_points`, `user_major`, `user_grade`, `session_expires_at`.
*   **`AppDatabase`:** Cleaned safely using `db.clearAllTables()`.
*   **`SharedPreferences`:** Any auxiliary cache files (e.g. `study_plan_catalog_cache`).

### 2.4. UI & Navigation Layer (`MainActivity.kt` / `ShetabNavGraph.kt`)
*   Observes `sessionManager.authEvents` with `repeatOnLifecycle(Lifecycle.State.STARTED)`.
*   On `AuthEvent.SessionExpired`:
    *   Executes:
        ```kotlin
        navController.navigate(Screen.Login.route) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
        ```
    *   Displays Persian Snackbar: «نشست شما منقضی شد. لطفاً مجدداً وارد شوید.»

---

## 3. RTL & Persian Localization
*   Toast / Snackbar message string stored in `res/values/strings.xml`:
    *   `<string name="error_session_expired">نشست کاربری شما منقضی شده است. لطفاً دوباره وارد شوید.</string>`
*   Full RTL layout preservation during transition.

---

## 4. Concurrency & Security Rules
*   **Thread Safety:** Local database clearing runs on `Dispatchers.IO` inside a supervised coroutine scope.
*   **Debouncing:** Multiple concurrent 401 responses in quick succession only trigger a single cleanup and a single navigation event.
