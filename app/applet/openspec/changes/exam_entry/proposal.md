# Proposal: Exam Entry by ID

## 1. Description
The user requested a feature to allow students to enter an exam using a specific Exam ID. A search box will be placed above the filters in the "Exams" (آزمون ها) screen. Upon entering a valid Exam ID and pressing search/enter, the user will be navigated to an "Exam Details" screen. This screen will display information about the exam and include an "Enter Exam" (ورود) button. Clicking the "Enter Exam" button will navigate the user to the actual Exam screen.

## 2. Acceptance Criteria
*   **Search Box in Exams Screen:**
    *   A text input field should be added at the top of the Exams screen (above existing filters).
    *   The field should accept an Exam ID.
    *   It should have a submit action (e.g., an icon or keyboard action).
*   **Exam Details Screen:**
    *   A new screen (e.g., `ExamDetailsScreen`) is created.
    *   When the user submits an ID from the search box, the app navigates to this new screen, passing the ID.
    *   The screen should display details (mocked or retrieved) about the exam (e.g., Exam Name, Duration, Questions count).
    *   The screen must include an "Enter" (ورود) button.
*   **Navigation to Exam:**
    *   Clicking the "Enter" button navigates the user to the actual active exam screen.
*   **RTL Compliance:** All UI elements must fully support Right-to-Left (RTL) layout.
