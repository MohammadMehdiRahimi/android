# Design: Auth Network Integration

## 1. Architecture (Clean Architecture & UDF)
We will follow the MVVM and Clean Architecture patterns specified in the project blueprint:
- **Network Layer:** Define data classes for request/response bodies and Retrofit interfaces for the endpoints. Retrofit/OkHttp timeouts will be set strictly to 30 seconds.
- **Repository Layer:** Abstract the network calls into an `AuthRepository` which maps responses to `NetworkResult` and parses HTTP error codes.
- **Domain Layer (Use Cases):** Introduce `SendOtpUseCase` and `VerifyOtpUseCase` to handle business logic (like phone number sanitization and code validation) and interact with the repository.
- **Presentation Layer (ViewModels):** Update `LoginViewModel` and `VerifyOtpViewModel` to use these Use Cases and manage the `UiState` via Kotlin Coroutines and `StateFlow`.

## 2. API Specifications
- **Base URL:** `https://api.weshetab.ir`
- **Timeout:** Exactly 30 seconds.

### Endpoints
**Send OTP**
- **Method:** `POST /auth/send-otp`
- **Request:** `{"phone": "String"}`
- **Response:** Success confirmation (200 OK).

**Verify OTP**
- **Method:** `POST /auth/verify-otp`
- **Request:** `{"phone": "String", "code": "String"}`
- **Response:** Auth and Refresh Tokens (or Set-Cookie headers) on success.

## 3. Data Flow & Sanitization
1. **User Input:** User inputs a phone number such as `09121111111` or `9121111111`.
2. **Sanitization:** The `SendOtpUseCase` (or a helper utility) converts this to `989121111111` to strictly match the backend regex `/^989\d{9}$/`.
3. **Network Call:** The network request uses the sanitized phone number.
4. **OTP Validation:** Before hitting the verify endpoint, ensure the OTP is exactly 6 digits.

## 4. Error Handling
The UI states will react to specific HTTP error codes mapped from the Repository layer:
- **429 (Rate Limit):** Present an error message indicating too many requests (limit: 5 for Send, 10 for Verify).
- **400 (Bad Request):** Present an "Invalid phone number" error.
- **401 (Unauthorized):** Present an "Incorrect or expired code" error during verification.

## 5. Token Storage
Upon successful OTP verification, tokens will be securely saved using the existing `TokenManager` logic.
