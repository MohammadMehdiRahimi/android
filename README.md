<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Run and deploy your AI Studio app

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/59a5a01d-b134-4495-be56-e71e3be1df49

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)


1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. Create a file named `.env` in the project directory, copy the values from `.env.example`, and set `API_BASE_URL` to your backend address. Set `GEMINI_API_KEY` too when the Gemini feature is needed.
5. Run the app on an emulator or physical device

## Server connection and authentication

The Shetab backend address has one source of truth: `API_BASE_URL` in the project-level `.env` file. For example:

```properties
# Android emulator connecting to a server running on the same computer
API_BASE_URL=http://10.0.2.2:4001/

# Physical phone connecting to the computer over the same Wi-Fi/LAN
# API_BASE_URL=http://192.168.1.10:4001/
```

After changing `.env`, sync/rebuild the Android project because Gradle generates `BuildConfig.API_BASE_URL` at build time. A trailing slash is optional. All Retrofit calls, refresh-token calls and relative backend file URLs use this value through `ApiClient`. Local HTTP hosts are allowed for development; use an HTTPS URL in production.

The Retrofit models follow the server's `{ body, status, statusCode }` envelope. OTP verification handles existing users and unknown phones separately; unknown users are created only after the server accepts name, grade and the conditional field-of-study selection loaded from `/base-info/onboarding`.

The refresh token is persisted as an HTTP-only cookie by `PersistentCookieJar`; `TokenAuthenticator` performs a synchronized refresh and retries the original request. Authorization, cookie and response-cookie values are redacted from logs.

The Group screen is API-driven and includes empty/search/create states, public joining and private requests, weekly member metrics, challenges, permanent badges, role/settings controls, automatic matchmaking, direct battle invitations, invitation approval and battle history.

## League feedback push notifications

The in-app notification inbox works from the server API. Firebase system push is temporarily commented out because the Android Firebase values are not available yet. Do not add blank `FIREBASE_*` keys to `.env`; the build ignores them for now.

When the values become available, re-enable the marked sections in `app/build.gradle.kts`, `AndroidManifest.xml`, `MainActivity.kt`, `ProfileScreen.kt`, and `ShetabFirebaseMessagingService.kt`. The server must separately receive `FCM_SERVICE_ACCOUNT_BASE64`; never place the server service-account JSON or its encoded value in the Android project.
