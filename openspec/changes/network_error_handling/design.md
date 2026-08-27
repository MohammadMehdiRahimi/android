# Design: Global Network Connection Error State & Retry System

## 1. Architectural Overview (Clean Architecture & UDF)

```text
               +----------------------------------+
               |        Retrofit / OkHttp         |
               +----------------------------------+
                                | (Network Error / Timeout / 5xx)
                                v
               +----------------------------------+
               |     NetworkResult.Exception      |
               |      or NetworkResult.Error      |
               +----------------------------------+
                                |
                                v
               +----------------------------------+
               |          Repository              |
               +----------------------------------+
                                |
                                v
               +----------------------------------+
               |            UseCase               |
               +----------------------------------+
                                |
                                v
               +----------------------------------+
               |     ViewModel (UiState)          |
               | (isNetworkError = true, etc.)    |
               +----------------------------------+
                                |
                                v
               +----------------------------------+
               |   Compose Presentation Layer     |
               |  NetworkErrorView(onRetry = ...) |
               +----------------------------------+
```

## 2. Reusable Component Design: `NetworkErrorView`
در مسیر `com.example.ui.core.components.NetworkErrorView.kt`:

### پارامترها:
- `title: String` (پیش‌فرض: «عدم برقراری ارتباط با سرور»)
- `description: String` (پیش‌فرض: «اتصال به اینترنت برقرار نشد. لطفاً وضعیت شبکه خود را بررسی کرده و مجدداً تلاش کنید.»)
- `onRetry: () -> Unit` (اکشن بازخوانی دیتا)
- `isRetrying: Boolean = false` (نمایش لودینگ چرخشی داخل دکمه در زمان تلاش مجدد)
- `modifier: Modifier = Modifier`
- `fullScreen: Boolean = true` (قابلیت استفاده تمام‌صفحه یا به صورت کارت درون لیست/محتوا)

### مشخصات استایل و تم:
- آیکون برداری مدرن با پس‌زمینه لطیف بنفش/قرمز (`Icons.Outlined.WifiOff` یا `Icons.Outlined.CloudOff`).
- تایپوگرافی: تیتر ضخیم با رنگ سرمه‌ای (`PlanNavy` / `0xFF18234D`) و متن توضیحی با رنگ خاکستری متمایل به سرمه‌ای (`0xFF7E859E`).
- دکمه اکشن برجسته: رنگ اصلی بنفش (`PlanPurple` / `0xFF7656F5`) همراه با آیکون `Icons.Outlined.Refresh` و انیمیشن لودینگ.
- `Modifier.testTag("network_error_view")` و `Modifier.testTag("network_retry_button")`.

## 3. Integration Matrix across Screens

| Screen / Feature | Data Source | Trigger Condition | Error Representation |
| :--- | :--- | :--- | :--- |
| **StudyPlanScreen** | `GET /study-tasks/me/daily` & `catalog` | `day == null && errorMessage != null` | Fullscreen `NetworkErrorView` with `onRetry = { viewModel.retry() }` |
| **CreateStudyPlanScreen** | `GET /study-tasks/me/catalog` | `catalog == null && error != null` | Inline & Step-based `NetworkErrorView` |
| **RegisterScreen** | `GET /base-info/onboarding` | `options.isEmpty() && isError` | Compact `NetworkErrorView` inside sheet/container |
| **AcademicReportScreen** | Server reports | `report == null && error != null` | Fullscreen `NetworkErrorView` |
| **AcademicLeaderboardScreen** | Leaderboard stats | `leaderboard == null && error != null` | Fullscreen `NetworkErrorView` |
| **TicketsScreen** | Tickets list | `tickets.isEmpty() && error != null` | Fullscreen / Empty-State `NetworkErrorView` |

## 4. Helper Utility & String Resources
افزودن کلیدهای ترجمه در `strings.xml`:
- `error_network_title`: عدم برقراری ارتباط با سرور
- `error_network_desc`: لطفاً اتصال اینترنت خود را بررسی نمایید و مجدداً تلاش کنید.
- `action_retry`: تلاش مجدد
- `error_server_unavailable`: سرور موقتاً در دسترس نیست.
