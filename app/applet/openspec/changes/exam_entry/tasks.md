# Tasks: Exam Entry by ID

## Phase 2: Implementation Checklist

- [x] **1. Domain & Data Layers:**
    - [x] Create `ExamDetails` data class.
    - [x] Create logic to fetch/generate tailored exam details based on ID and mock data.

- [x] **2. Presentation Layer - Search Box:**
    - [x] Add an OutlinedTextField / Search Card for "ورود با شناسه آزمون" above the filters in `ExamsScreen`.
    - [x] Handle input state in `ExamsScreen`.
    - [x] Setup keyboard action (ImeAction.Go) and submit button to navigate to `exam_details/{id}`.

- [x] **3. Presentation Layer - Exam Details Screen:**
    - [x] Create `ExamDetailsUiState`.
    - [x] Create `ExamDetailsViewModel` that accepts `examId` and fetches details.
    - [x] Create `ExamDetailsScreen` UI using Compose (RTL compliant, display exam details, specifications, rules, and "ورود به آزمون" button).

- [x] **4. Navigation Integration:**
    - [x] Add `exam_details/{examId}` route in `MainActivity.kt`.
    - [x] Connect the "ورود به آزمون" (Enter) button to navigate to `exam_taking`.

- [x] **5. Testing:**
    - [x] Add unit tests for `ExamDetailsViewModel`.
    - [x] Add UI tests for the Search Box and `ExamDetailsScreen`.
