# Proposal: Registration (Onboarding) Feature

## 1. Description
This proposal outlines the development of the Registration Screen, serving as the onboarding step for users to provide their basic information (Full Name, Grade, and Field of Study) after initial authentication.

## 2. Objectives
- Fetch required onboarding data (grades and fields of study) from the server upon screen load.
- Provide a clean, RTL-compliant, Pixel-Perfect UI for users to select their grade and field of study using selectable boxes (chips) instead of traditional dropdowns.
- Handle conditional UI logic: hide the "Field of Study" section for lower grades (e.g., 5th to 9th).
- Collect user's full name via a text input field.
- Ensure the selected `key` (not the displayed `value`) is prepared for backend submission.

## 3. Acceptance Criteria
1. **API Integration:** The app must successfully make a `GET` request to `/base-info/onboarding` without an `Authorization` header and parse the JSON response containing `grades` and `fieldsOfStudy`.
2. **Skeleton Loading:** Until the API response is received, the screen should display a skeleton loading state for the selectable boxes.
3. **UI Layout:** The screen must strictly match the provided UI design, utilizing Jetpack Compose, right-to-left (RTL) layout, and Persian typography (Vazirmatn/IranSans).
4. **Interactive Selection:** Grades and fields of study must be displayed as selectable chips. Clicking a chip updates its visual state (background color, text color, checkmark icon for selected state).
5. **Conditional Logic:** If a user selects a grade from 5th to 9th (e.g., `FIFTH`, `SIXTH`, `SEVENTH`, `EIGHTH`, `NINTH`), the "Field of Study" section should be hidden or disabled. For grades 10th to 12th (`TENTH`, `ELEVENTH`, `TWELFTH`), it must be visible and mandatory.
6. **Data Preparation:** When the user clicks "Continue" (ادامه), the ViewModel should correctly hold the full name, the selected grade `key`, and the selected field of study `key` (if applicable).
