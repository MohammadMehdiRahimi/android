# Technical Design: Event-Driven Study Task Focus Timer & Execution API Integration

## 1. Architecture Overview (Clean Architecture & UDF)

The implementation strictly follows the Feature-First Clean Architecture structure:
- **Data Layer (`network/`, `data/repository/`):** Moshi-serialized request/response DTOs, Retrofit endpoint definitions, and repository implementations.
- **Domain Layer (`domain/usecase/`):** Use cases encapsulating business invariants, UUID generation, ISO-8601 UTC timestamp generation, and event sequencing.
- **Presentation Layer (`ui/features/studyplan/`):** `FocusTimerViewModel` managing active execution state, sequence counts, timer ticks, and `FocusTimerScreen` providing Persian RTL UI and interaction dialogs.

```text
FocusTimerScreen (Compose UI / RTL)
       ▲
       │ UI State (Flow) / User Events
       ▼
FocusTimerViewModel (Sequence & Timer State)
       ▲
       │
       ▼
SendManualTaskEventUseCase / MarkTaskDoneUseCase
       ▲
       │
       ▼
StudyTaskRepository
       ▲
       │
       ▼
ApiService (`POST /study-execution/me/manual-tasks/{taskId}/events`)
```

---

## 2. Data Models & API Contract (`network/ApiService.kt`)

### 2.1 Enums & Constants
```kotlin
enum class StudyExecutionEventType {
    ACTIVITY_STARTED,
    ACTIVITY_PAUSED,
    ACTIVITY_RESUMED,
    ACTIVITY_COMPLETED,
    ACTIVITY_MARKED_DONE
}

enum class CompletionOutcome {
    FULL,
    PARTIAL
}

enum class ExecutionStatus {
    ACTIVE,
    PAUSED,
    COMPLETED,
    PARTIAL,
    SKIPPED
}
```

### 2.2 Request DTO (`ManualTaskEventRequestDto`)
```kotlin
@JsonClass(generateAdapter = true)
data class ManualTaskEventRequestDto(
    @Json(name = "clientEventId") val clientEventId: String, // UUID v4
    @Json(name = "expectedSequence") val expectedSequence: Int,
    @Json(name = "type") val type: String, // from StudyExecutionEventType
    @Json(name = "occurredAt") val occurredAt: String, // ISO-8601 UTC (e.g. 2026-08-25T10:00:00.000Z)
    @Json(name = "completionOutcome") val completionOutcome: String? = null, // FULL | PARTIAL
    @Json(name = "completionPercent") val completionPercent: Int? = null, // 1..100
    @Json(name = "note") val note: String? = null
)
```

### 2.3 Response DTO (`ManualTaskExecutionResponseDto`)
```kotlin
@JsonClass(generateAdapter = true)
data class ManualTaskExecutionResponseDto(
    @Json(name = "id") val id: String,
    @Json(name = "manualTaskId") val manualTaskId: String,
    @Json(name = "status") val status: String, // ACTIVE, PAUSED, COMPLETED, PARTIAL, SKIPPED
    @Json(name = "eventSequence") val eventSequence: Int,
    @Json(name = "actualSeconds") val actualSeconds: Int? = 0,
    @Json(name = "persistedActiveSeconds") val persistedActiveSeconds: Int? = 0,
    @Json(name = "timerElapsedSeconds") val timerElapsedSeconds: Int? = 0,
    @Json(name = "pausedSeconds") val pausedSeconds: Int? = 0,
    @Json(name = "completionPercent") val completionPercent: Int? = null,
    @Json(name = "startedAt") val startedAt: String? = null,
    @Json(name = "finishedAt") val finishedAt: String? = null
)
```

### 2.4 Retrofit Endpoint Definition
```kotlin
@POST("study-execution/me/manual-tasks/{taskId}/events")
suspend fun sendManualTaskEvent(
    @Path("taskId") taskId: String,
    @Body request: ManualTaskEventRequestDto
): ApiResponse<ManualTaskExecutionResponseDto>
```

---

## 3. Domain Layer (`domain/usecase/`)

### 3.1 `SendManualTaskEventUseCase`
Handles the business logic for preparing the event payload:
- Generates `UUID.randomUUID().toString()` for `clientEventId`.
- Formats current instant into ISO-8601 UTC string (`Instant.now().toString()`).
- Passes parameters to repository and returns `NetworkResult<ManualTaskExecutionResponseDto>`.

### 3.2 ISO-8601 Formatting Helper
```kotlin
object EventTimeHelper {
    fun currentIsoUtc(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return sdf.format(Date())
    }
}
```

---

## 4. State Management (`FocusTimerViewModel`)

### 4.1 State Representation
```kotlin
data class FocusTimerUiState(
    val taskId: String = "",
    val taskTitle: String = "",
    val plannedMinutes: Int = 45,
    val elapsedSeconds: Int = 0,
    val remainingSeconds: Int = 45 * 60,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false,
    val currentSequence: Int = 0,
    val lastExecutionStatus: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showCompletionDialog: Boolean = false,
    val completionPercent: Int = 100,
    val completionNote: String = "",
    val completionOutcome: CompletionOutcome = CompletionOutcome.FULL
)
```

### 4.2 Lifecycle Transitions & Sequence Progression
1. **Initial Load:** `currentSequence = 0`.
2. **On Start Click:**
   - Dispatches `type = ACTIVITY_STARTED`, `expectedSequence = 0`.
   - On Success: `currentSequence = response.eventSequence`, `isRunning = true`, `isPaused = false`, starts timer coroutine ticker.
3. **On Pause Click:**
   - Dispatches `type = ACTIVITY_PAUSED`, `expectedSequence = currentSequence`.
   - On Success: `currentSequence = response.eventSequence`, `isRunning = false`, `isPaused = true`, halts timer ticker.
4. **On Resume Click:**
   - Dispatches `type = ACTIVITY_RESUMED`, `expectedSequence = currentSequence`.
   - On Success: `currentSequence = response.eventSequence`, `isRunning = true`, `isPaused = false`, resumes timer ticker.
5. **On Finish / Complete Click:**
   - Opens Completion Dialog: Allows student to select "پایان کامل" (100% Full) or "پایان نیمه‌کاره" (Partial % with optional note).
   - Dispatches `type = ACTIVITY_COMPLETED`, `expectedSequence = currentSequence`, outcome, percent, and note.
   - On Success: `isCompleted = true`, displays celebration/reward and navigates back.
6. **Direct "Mark as Done" (Quick Complete):**
   - Dispatches `type = ACTIVITY_MARKED_DONE`, `expectedSequence = 0`.
   - On Success: Task status is marked completed in study plan list.

### 4.3 Error Reconciliation (409 Conflict)
If server responds with 409 Conflict (`EXECUTION_SEQUENCE_MISMATCH`):
- Show localized Persian notification: *"عدم تطابق توالی رویداد. در حال همگام‌سازی با سرور..."*
- Optionally reset sequence or re-fetch active execution status.

---

## 5. Presentation Layer & UI Experience

1. **RTL Support & Persian Typography:**
   - All text rendered with Vazirmatn font.
   - Persian digit translation for timer strings (`۰۰:۴۵:۰۰`).
2. **Interactive Controls:**
   - Large Circular Progress Timer with start/pause/resume states.
   - Distinct Action Buttons with TestTags: `timer_start_button`, `timer_pause_button`, `timer_resume_button`, `timer_finish_button`.
3. **Completion BottomSheet / Dialog:**
   - Option 1: "مطالعه کامل شد (۱۰۰٪)" -> Sends FULL 100%.
   - Option 2: "مطالعه ناقص ماند" -> Slider/NumberPicker for percentage (1-99%) + Persian text input for notes -> Sends PARTIAL.

---

## 6. Testing Strategy
1. **Unit Tests (`FocusTimerViewModelTest`):**
   - Verifies sequence progression across Start -> Pause -> Resume -> Complete.
   - Verifies UUID and ISO-8601 generation.
   - Verifies 409 error handling and state rollback/reconciliation.
2. **Domain Tests (`SendManualTaskEventUseCaseTest`):**
   - Tests payload building for each of the 6 scenarios.
3. **Robolectric & UI Tests (`FocusTimerScreenTest`):**
   - Tests button clicks, timer display, dialog interaction, and RTL rendering.
