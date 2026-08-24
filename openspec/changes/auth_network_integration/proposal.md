# Proposal: Auth Network Integration (Send OTP & Verify OTP)

## 1. Description
This feature integrates the `Send OTP` and `Verify OTP` flows with the real backend API (`https://api.weshetab.ir`). The goal is to allow users to authenticate using their phone numbers and a 6-digit OTP code, connecting the presentation layer logic to actual network calls via Clean Architecture.

## 2. Acceptance Criteria
- [ ] Users can enter a phone number (with or without a leading zero).
- [ ] The phone number is sanitized to exactly match the `/^989\d{9}$/` format before being sent to the server.
- [ ] Sending the phone number calls `POST /auth/send-otp`.
- [ ] Users can enter a 6-digit OTP code.
- [ ] Submitting the OTP calls `POST /auth/verify-otp`.
- [ ] The app properly handles rate limits (HTTP 429), validation errors (HTTP 400), and invalid/expired codes (HTTP 401), showing appropriate UI error states.
- [ ] On successful verification, Auth Token and Refresh Token are securely stored (e.g., using `TokenManager` with EncryptedSharedPreferences).
