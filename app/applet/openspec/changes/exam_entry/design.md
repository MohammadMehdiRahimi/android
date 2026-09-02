# Design: Exam Entry by ID

## 1. Feature Architecture
This feature spans two main presentation areas: the existing Exams list screen and a new Exam Details screen.

### 1.1 Data/Domain
*   **Domain Models:** `ExamDetails` (id, title, duration, totalQuestions).
*   **Repository:** Add a method in `ExamRepository` to fetch exam details by ID. For now, it will return mocked data or use a simple use case.
*   **UseCase:** `GetExamDetailsUseCase` to fetch details.

### 1.2 Presentation
*   **Exams Screen (`ui/features/exams/`):**
    *   Update the existing `ExamsScreen` or `ExamsViewModel` to handle search input state.
    *   Add a `TextField` (OutlinedTextField) wrapped in a Composable above the filters.
*   **Exam Details Screen (`ui/features/exams/details/`):**
    *   `ExamDetailsScreen`: Displays the retrieved exam info.
    *   `ExamDetailsViewModel`: Manages the state (`Loading`, `Success`, `Error`) of fetching exam details by ID.
    *   `ExamDetailsUiState`: Holds the exam data.

## 2. Navigation Flow
*   **Route:** `exams` -> User enters ID and submits.
*   **Route:** `exam_details/{examId}` -> ExamDetailsScreen is pushed.
*   **Route:** `active_exam/{examId}` -> When "ورود" is clicked, navigates to the actual exam screen.

## 3. RTL & Theming
*   Use `MaterialTheme` colors.
*   Ensure text aligns correctly for Persian using `start`/`end` paddings.
*   The Search Box uses an action icon `Icons.AutoMirrored.Filled.ArrowForward` (or a search icon) that handles RTL flipping natively.

## 4. State Management (UDF)
*   **Search Box state:** Kept in `ExamsViewModel` or local state if it only triggers navigation.
*   **Details state:** `ExamDetailsViewModel` emits `StateFlow<ExamDetailsUiState>` initialized with the ID passed via navigation arguments.
