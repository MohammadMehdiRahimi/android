# Tasks: Auth Network Integration

## Phase 2: Implementation Checklist

### 1. Network Layer & Configuration
- [ ] Verify/Update `ApiClient.kt` (OkHttpClient configuration) to enforce exactly a 30-second timeout for connect, read, and write operations.
- [ ] Create Request/Response data classes (e.g., `SendOtpRequest`, `VerifyOtpRequest`, `AuthResponse`).
- [ ] Add `POST /auth/send-otp` and `POST /auth/verify-otp` endpoints to `ApiService.kt` (or a dedicated `AuthApiService`).

### 2. Repository Layer
- [ ] Create `AuthRepository` interface and `AuthRepositoryImpl` class.
- [ ] Implement `sendOtp(phone: String): Flow<NetworkResult<Unit>>` with HTTP error parsing (400, 429).
- [ ] Implement `verifyOtp(phone: String, code: String): Flow<NetworkResult<AuthResponse>>` with HTTP error parsing (401, 429).

### 3. Domain Layer (Use Cases)
- [ ] Create `SendOtpUseCase`: Implement logic to sanitize phone numbers (handling `09` and `9` prefixes) to the `989...` format.
- [ ] Create `VerifyOtpUseCase`: Implement logic to validate the code is exactly 6 digits before proceeding, and upon success, trigger token storage.

### 4. Presentation Layer (ViewModels)
- [ ] Update `LoginViewModel` to integrate `SendOtpUseCase`. Handle UI loading and map network errors (400, 429) to user-friendly messages via `LoginUiState`.
- [ ] Update `VerifyOtpViewModel` to integrate `VerifyOtpUseCase`. Handle UI loading and map network errors (401, 429) to user-friendly messages via `VerifyOtpUiState`.

### 5. Tests (Mandatory)
- [ ] Write Unit Tests for `SendOtpUseCase` to verify phone number sanitization logic.
- [ ] Write Unit Tests for `VerifyOtpUseCase` validation.
- [ ] Write Unit Tests for `LoginViewModel` and `VerifyOtpViewModel` state transitions based on different network results (Success, 400, 401, 429).
