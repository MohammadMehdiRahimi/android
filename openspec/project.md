# Project Blueprint: Shetab (شتاب)

## 1. Project Overview
**Shetab (شتاب)** is a comprehensive, production-grade educational Android application tailored for Iranian and Persian-speaking students. Its core domain encompasses:
*   **Authentication & Onboarding:** Seamless onboarding and mobile-based OTP authentication with Persian phone number formatting/validation.
*   **Study Planning & Focus Timer:** Smart study planning wizard, daily task schedules, and Pomodoro/focus timer.
*   **Academic Exams & Test Building:** Interactive exam taking, custom exam generation, comprehensive answer reviews with LaTeX math formula rendering.
*   **Flashcards & Spaced Repetition:** Local database-backed Leitner/flashcard system for active recall.
*   **Gamification & Community:** Academic leaderboards, competitive leagues, study groups, medal showcases, and personal growth analytics.
*   **AI Assistant (Raya):** Integrated AI-driven educational chat assistant.
*   **RTL & Localization:** Fully native Right-to-Left (RTL) layout support, customized Persian typography (Vazirmatn, IranSans), and Persian numeral conversion across all screens.

---

## 2. Tech Stack & Dependencies
*   **Language & Runtime:** Kotlin (Targeting Java 11 / JVM target 11)
*   **UI Framework:** Jetpack Compose with Material 3 (M3)
*   **Asynchronous Programming:** Kotlin Coroutines (`viewModelScope`, `Dispatchers.IO`) & `StateFlow` / `SharedFlow`
*   **Networking:** Retrofit2 & OkHttp3 with custom interceptors (`AuthInterceptor`, `ResponseInterceptor`, `TokenAuthenticator`), Moshi (with KSP code generation) for JSON serialization/deserialization.
*   **Local Storage:** Room Database (SQLite with KSP annotation processing) for on-device persistence (e.g., flashcard entities and Leitner intervals), `SharedPreferences` for token storage (`TokenManager`).
*   **Image & Vector Loading:** Coil (with SVG decoder integration).
*   **Mathematical Rendering:** AndroidView with MathJax / Web-based Latex rendering (`LatexText`).
*   **Navigation:** Jetpack Navigation Compose with animated transitions (`slideIntoContainer` / `slideOutOfContainer`).
*   **Dependency Injection (DI):** Manual / Constructor Injection with singleton service managers (e.g., `ApiClient`) and `AndroidViewModelFactory`.
*   **Testing Frameworks:**
    *   **Unit Tests:** JUnit 4, Kotlin Coroutines Test.
    *   **Android JVM Tests:** Robolectric (`@RunWith(RobolectricTestRunner::class)`).
    *   **Compose UI & Screenshot Tests:** `androidx.compose.ui.test.junit4`, Roborazzi (`@GraphicsMode(GraphicsMode.Mode.NATIVE)`).

---

## 3. Architecture & Modularization
The codebase strictly follows **Clean Architecture** patterns within a **Feature-First** modular organization, driven by **Unidirectional Data Flow (UDF)** and **MVVM** (Model-View-ViewModel).

### Architectural Layers
1.  **Presentation Layer (`ui/`):**
    *   **Compose Screens:** Pure UI functions observing immutable state and dispatching user intents.
    *   **ViewModels (`*ViewModel`):** Retain business state across config changes, encapsulate validation and processing logic, and expose a single immutable `StateFlow<UiState>`.
    *   **UI States (`*UiState`):** Strongly typed, immutable data classes containing all screen variables (loading, error, validation status, form fields).
2.  **Domain & Data Layers (`data/`, `network/`):**
    *   **Repositories (`data/repository/*`):** Single source of truth abstracting data sources (Room DAOs vs. Retrofit API services).
    *   **Network Execution:** Safe API execution utilizing `safeApiCall` wrapper returning `NetworkResult.Success`, `NetworkResult.Error`, or `NetworkResult.Exception`.
    *   **Local Persistence:** Room Database (`AppDatabase`, `FlashcardDao`, `FlashcardEntity`).

### Feature-First Structure
All screen-specific logic (Composable screens, subcomponents, ViewModels, and UI state models) is grouped by feature inside `ui/features/<feature_name>/` (e.g., `auth/login`, `studyplan`, `exams`, `flashcards`).

---

## 4. Directory Structure
```text
/app/src/
├── main/
│   ├── assets/                # Mock questions JSON, static SVGs
│   ├── java/com/example/
│   │   ├── data/              # Data Layer
│   │   │   ├── local/         # Room Database, DAOs, Entities
│   │   │   ├── repository/    # Repositories (e.g., FlashcardRepository)
│   │   │   └── ...            # Mock data loaders, Gemini service
│   │   ├── network/           # API Client & Services
│   │   │   ├── ApiClient.kt   # Retrofit/OkHttp initialization
│   │   │   ├── ApiService.kt  # Endpoints definition
│   │   │   ├── TokenManager.kt# Token persistence and auth status
│   │   │   └── ...            # Interceptors & custom adapters
│   │   ├── notifications/     # Push notification handling
│   │   ├── ui/                # Presentation Layer
│   │   │   ├── core/          # Shared components (AppBackground, Shimmer, LatexText)
│   │   │   ├── features/      # Feature-First Modules
│   │   │   │   ├── auth/      # Auth sub-features
│   │   │   │   │   └── login/ # LoginScreen, LoginViewModel, LoginUiState
│   │   │   │   ├── academicleaderboard/
│   │   │   │   ├── academicreport/
│   │   │   │   ├── dashboard/
│   │   │   │   ├── exams/
│   │   │   │   ├── flashcards/
│   │   │   │   ├── leaderboard/
│   │   │   │   ├── mygroup/
│   │   │   │   ├── notifications/
│   │   │   │   ├── onboarding/
│   │   │   │   ├── personalgrowth/
│   │   │   │   ├── premium/
│   │   │   │   ├── profile/
│   │   │   │   ├── raya/
│   │   │   │   ├── studyplan/
│   │   │   │   ├── tickets/
│   │   │   │   └── trouble/
│   │   │   ├── main/          # ShetabApp Scaffold, BottomNavigation, MainScreen
│   │   │   ├── screens/       # Legacy/wrapper auth & splash screens
│   │   │   └── theme/         # AppTheme, Colors, Typography (Vazirmatn/IranSans)
│   │   └── MainActivity.kt    # Entry activity, NavHost, RTL CompositionLocal
│   └── res/                   # Drawables, Fonts, Values (strings.xml, colors.xml)
├── test/java/com/example/     # Unit & Robolectric JVM Tests
│   ├── ExampleRobolectricTest.kt
│   ├── ExampleUnitTest.kt
│   ├── GreetingScreenshotTest.kt
│   ├── network/               # Adapter & network parsing tests
│   └── ui/features/
│       └── auth/login/        # LoginViewModelTest, LoginScreenTest
└── ...
/openspec/                     # Specification-Driven Development
├── project.md                 # Global Blueprint (this file)
└── changes/                   # Feature-specific proposals, designs, and tasks
    └── login_feature/
        ├── proposal.md
        ├── design.md
        └── tasks.md
```

---

## 5. Coding Conventions & Standards
*   **State Management (UDF):** Every feature must expose an immutable `StateFlow<T>` from its ViewModel. State mutations must be done atomically via `.update { copy(...) }`.
*   **RTL Enforcement:** The entire UI is wrapped in `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl)`.
    *   Never use `left`/`right` in modifiers or padding. Use `start` and `end`.
    *   Use `Icons.AutoMirrored` for directional icons (e.g., arrow back/forward) to ensure correct flipping in RTL mode.
*   **Persian Formatting:** Digits and phone numbers entered by users should be converted from Persian/Arabic digits to English digits for internal validation and API transmission, and displayed with Persian typography where appropriate.
*   **Performance:**
    *   Always use `LazyColumn`, `LazyRow`, or `LazyVerticalGrid` for dynamic or long lists.
    *   Stabilize composable parameters and use `remember` / `derivedStateOf` to prevent unnecessary recompositions.
*   **Strings & Localization:** No hardcoded strings in Composable functions. All user-facing texts must reside in `res/values/strings.xml` with descriptive semantic keys.
*   **UI Identifiers & TestTags:** Interactive elements (buttons, inputs, cards) must define semantic test tags using `Modifier.testTag("snake_case_name")` (e.g., `login_phone_input`, `login_submit_button`).

---

## 6. Testing Strategy
*   **Framework:** Robolectric is used for running all Android Context and Compose UI tests directly on the JVM without an emulator.
*   **Unit Tests (`*ViewModelTest`):** Test ViewModel state transitions, input validation, normalization, and business logic without Android UI dependencies.
*   **Compose UI Tests (`*ScreenTest`):** Test UI element visibility, user interactions (`performTextInput`, `performClick`), and theme/RTL composition rules using `createComposeRule()`.
*   **Visual Regression Tests:** Roborazzi integration for recording and verifying UI screenshots across different themes (`PESARANE` / `DOKHTARANE`) and screen qualifiers.
*   **Directory Alignment:** Test classes mirror the package structure of production code under `app/src/test/java/com/example/`.
