# Design: Registration (Onboarding) Feature

## 1. Architecture & State Management (UDF)
This feature will be built using the Clean Architecture and MVI/UDF pattern as specified in the global blueprint.

### 1.1 UI State (`RegisterUiState`)
The state will hold both the fetched data and the user's current selections.

```kotlin
data class RegisterUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val grades: List<BaseInfoItem> = emptyList(),
    val fieldsOfStudy: List<BaseInfoItem> = emptyList(),
    
    // User Input State
    val fullName: String = "",
    val selectedGradeKey: String? = null,
    val selectedFieldKey: String? = null,
    
    // Derived State
    val isFieldOfStudyRequired: Boolean = false,
    val isSubmitEnabled: Boolean = false
)

data class BaseInfoItem(
    val key: String,
    val value: String
)
```

### 1.2 View Model (`RegisterViewModel`)
- Uses `viewModelScope` and `StateFlow` to manage the UI state.
- **Initialization:** Triggers `fetchBaseInfo()` on init.
- **Events:** `onFullNameChanged(String)`, `onGradeSelected(String)`, `onFieldSelected(String)`, `onSubmit()`.
- **Conditional Logic:** In `onGradeSelected`, if the selected key represents a lower grade (e.g., `"FIFTH"` to `"NINTH"`), `isFieldOfStudyRequired` is set to false, and `selectedFieldKey` is cleared. Otherwise, it is set to true.
- **Validation:** `isSubmitEnabled` is derived by ensuring `fullName` is not blank, `selectedGradeKey` is not null, and if `isFieldOfStudyRequired` is true, `selectedFieldKey` must also not be null.

## 2. Domain & Data Layer

### 2.1 API Endpoint (`ApiService`)
```kotlin
interface ApiService {
    @GET("base-info/onboarding")
    suspend fun getOnboardingBaseInfo(): Response<OnboardingBaseInfoResponse>
}
```
*Note: We will configure Retrofit/OkHttp or a specific Interceptor annotation (e.g., `@Headers("No-Authentication: true")`) to ensure this endpoint bypasses the `AuthInterceptor`.*

### 2.2 Response Model
```kotlin
@JsonClass(generateAdapter = true)
data class OnboardingBaseInfoResponse(
    @Json(name = "grades") val grades: List<BaseInfoItemDto>,
    @Json(name = "fieldsOfStudy") val fieldsOfStudy: List<BaseInfoItemDto>
)

@JsonClass(generateAdapter = true)
data class BaseInfoItemDto(
    @Json(name = "key") val key: String,
    @Json(name = "value") val value: String
)
```

### 2.3 Repository (`AuthRepository` / `BaseInfoRepository`)
Implement `getOnboardingBaseInfo(): Flow<NetworkResult<OnboardingBaseInfoResponse>>` to abstract the API call, mapping the response to domain entities if necessary, or directly passing to the ViewModel.

## 3. UI Design (Jetpack Compose)

### 3.1 Screens & Layouts
- **Main Screen (`RegisterScreen.kt`):** A scrollable `Column` with `Scaffold`.
- **Top Section:** Header text ("اطلاعات خود را وارد کنید") with an illustration/icon.
- **Input Fields:** 
  - `OutlinedTextField` or a custom styled TextField for Full Name with a leading/trailing icon.
- **Selection Boxes (Chips):**
  - Custom `SelectableChip` composable.
  - Displays `item.value` (e.g., "دهم").
  - `onClick` triggers the selection logic passing `item.key`.
  - Visual states:
    - **Unselected:** White/Light background, gray/purple border, default text color.
    - **Selected:** Solid Purple background, white text, checkmark icon.
- **Skeleton Loader:** Use the existing `Shimmer` component over the chip grid while `isLoading` is true.

### 3.2 RTL & Theming
- Ensure all text and icons align correctly in RTL (Right-to-Left) direction.
- Use `Vazirmatn` or `IranSans` for all typography.
- Use semantic colors from `MaterialTheme.colorScheme` (e.g., Primary for selected chips).
