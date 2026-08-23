# Proposal: Login Feature (Phone Number & OTP Request)

## 1. Feature Overview
The Login screen allows users to authenticate into the Shetab application using their mobile phone numbers. Upon entering a valid phone number, the user requests a One-Time Password (OTP) for verification. 

## 2. Acceptance Criteria
- **Pixel-Perfect UI:** UI implementation based on the provided design.
- **RTL & Typography:** Native Right-to-Left (RTL) layout support, using Persian typography (Vazirmatn/IranSans) as specified in `project.md`.
- **Input Field:** Mobile number input field with country code prefix (+98), Iranian flag, and vertical divider.
- **Validation:** Input validation to ensure only digits are entered and the number is of valid length (11 digits starting with 09, or 10 digits without zero).
- **Navigation:** The screen sits right after the Onboarding screen. Pressing back from this screen must not return to Onboarding.
- **Terms Footer:** Terms and Conditions footer text with clickable styled text.
- **Resources:** Move the `send_otp_vecotr.png` to the `drawable` resources and rename to `send_otp_vector.png` (Already completed during Phase 0).

## 3. Out of Scope
- The actual OTP verification screen (where the user enters the code) is out of scope for this specific feature PR, though navigation to it will be prepared.
- Complex country code selection (dropdown functionality) is out of scope. It will be visually static as per standard single-country apps.
