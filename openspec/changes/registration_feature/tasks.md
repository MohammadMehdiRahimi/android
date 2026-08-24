# Tasks: Registration (Onboarding) Feature

## Phase 2: Implementation Checklist

### 1. Data Layer & Networking
- [ ] Define `OnboardingBaseInfoResponse` and `BaseInfoItemDto` data classes with `@JsonClass(generateAdapter = true)` in the `network` or `data/model` package.
- [ ] Add the `getOnboardingBaseInfo` endpoint to `ApiService` as a `GET` request.
- [ ] Update `AuthInterceptor` (or use specific annotations) to allow this endpoint to be called without an Authorization token.
- [ ] Add the fetching logic to `AuthRepository` (or create a new repository like `BaseInfoRepository`) using `safeApiCall`.

### 2. Presentation Logic (ViewModel)
- [ ] Create `RegisterUiState` to hold loading status, data lists, and user selections.
- [ ] Create `RegisterViewModel`.
- [ ] Implement `fetchBaseInfo` in `RegisterViewModel` to load data on initialization and map `NetworkResult` to `RegisterUiState`.
- [ ] Implement intent functions: `onFullNameChanged`, `onGradeSelected`, `onFieldSelected`.
- [ ] Implement conditional logic for `isFieldOfStudyRequired` (e.g., hiding field of study if grade is < 10th).
- [ ] Implement logic to derive `isSubmitEnabled` based on all required fields being valid.

### 3. UI Development (Jetpack Compose)
- [ ] Create a custom `SelectableChip` component that matches the Figma/Image design (supports selected/unselected visual states and an optional checkmark).
- [ ] Create `RegisterScreen` composable.
- [ ] Implement the Header section (Icon/Illustration + Title + Subtitle).
- [ ] Implement the Full Name text field section matching the design (rounded borders, specific typography, leading icon).
- [ ] Implement the Grade Selection section using a grid or wrapped layout of `SelectableChip`s.
- [ ] Implement the Field of Study Selection section (visible conditionally based on state).
- [ ] Implement Skeleton Loading (using `Shimmer`) for the grade/field sections when `isLoading` is true.
- [ ] Implement the bottom "Continue" (ادامه) button, bound to `isSubmitEnabled`.

### 4. Integration & Navigation
- [ ] Add `RegisterScreen` to the application's navigation graph (`MainActivity` or designated NavGraph).
- [ ] Pass the appropriate parameters and navigation callbacks to `RegisterScreen`.

### 5. Testing
- [ ] Write unit tests for `RegisterViewModel` validating the state transitions, conditional logic for fields of study, and submit button enablement.
- [ ] Write Compose UI tests for `RegisterScreen` to verify RTL layout, skeleton loading visibility, and chip interaction.
- [ ] Run Roborazzi screenshot tests if applicable.
