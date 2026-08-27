# Feature Proposal: Auth 401 Session Cleanup & Auto-Redirect (مدیریت خطای ۴۰۱، پاکسازی داده‌ها و انتقال به لاگین)

## 1. Context & Background
In the Shetab application, user authentication is managed via JWT tokens stored in `TokenManager`, session cookies in `PersistentCookieJar`, and localized user/plan data persisted in the on-device `AppDatabase` (Room) and `SharedPreferences`.

When an access token expires or is revoked, and the refresh attempt fails or any authenticated API returns an unrecoverable `401 Unauthorized` response, the app must safely and completely purge all sensitive stored device data (session tokens, user profile, cached study plans, tasks, flashcards, draft caches) and immediately transition the user back to the Login screen (`Screen.Login.route`) to prevent unauthorized access, stale UI states, or lingering credentials.

---

## 2. Objectives (The "What" & "Why")
*   **What:**
    1.  Detect HTTP `401 Unauthorized` responses in OkHttp interceptors (`ResponseInterceptor`, `TokenAuthenticator`) and API calls.
    2.  Purge all on-device cached data including:
        *   `TokenManager` session tokens (JWT, refresh token, user ID, profile, phone, expiry timestamps).
        *   `PersistentCookieJar` session cookies.
        *   `AppDatabase` Room database tables (study plans, tasks, flashcards, Leitner items).
        *   Any cached SharedPreferences (catalog cache, temporary drafts).
    3.  Emit a global `AuthEvent.SessionExpired` event via a reactive channel (`SharedFlow`).
    4.  Observe the event in the root UI layer (`MainActivity` / `ShetabNavGraph`) to reset the navigation stack (`popUpTo(0) { inclusive = true }`) and redirect the user smoothly to the Login screen with an informative Persian feedback message (e.g. «نشست شما منقضی شده است. لطفاً دوباره وارد شوید»).

*   **Why:**
    *   **Security:** Prevents unauthorized viewing of cached academic and personal data after token invalidation.
    *   **User Experience (UX):** Avoids confusing persistent errors, broken loading loops, and keeps the user flow clear and uninterrupted.
    *   **Clean State:** Ensures that when another user (or the same user) logs in again, they start with a clean slate without colliding with previous user artifacts.

---

## 3. Acceptance Criteria
1.  **401 Trigger:** Receiving an unrecoverable HTTP `401` from any Retrofit endpoint triggers the session cleanup flow.
2.  **Comprehensive Data Purge:**
    *   `TokenManager` tokens and preferences are cleared (`clearAllData()`).
    *   `PersistentCookieJar` cookies are flushed.
    *   `AppDatabase.clearAllTables()` is executed asynchronously on `Dispatchers.IO`.
    *   Local catalog and draft caches are wiped.
3.  **UI & Navigation Reset:**
    *   The app navigates to the Login screen (`Screen.Login.route`).
    *   The navigation backstack is completely cleared so the back button does not return to protected screens.
    *   A snackbar or toast notifies the user of session expiration in Persian.
4.  **No Crash on Background Thread:** Data clearance and navigation event dispatching handle concurrency safely without blocking the UI thread or throwing lifecycle exceptions.
5.  **Automated Tests:** Unit and Robolectric tests verify token clearing, event emission, and navigation trigger upon HTTP 401.
