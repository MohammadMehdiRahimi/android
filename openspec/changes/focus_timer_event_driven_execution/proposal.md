# Proposal: Event-Driven Study Task Focus Timer & Execution API Integration

## 1. Overview & Problem Statement
Currently, study task updates in the focus timer and study plan flows require synchronization with the backend study execution engine. Instead of updating entire task records monolithically, the backend employs a robust **Event-Driven Architecture** for managing manual study tasks. 

Every lifecycle transition of a manual task (starting the timer, pausing, resuming, completing with full or partial progress, or directly marking as done) must be dispatched as an immutable event with sequence tracking (`expectedSequence`) and idempotency support (`clientEventId`).

This proposal specifies the end-to-end integration of the `POST /study-execution/me/manual-tasks/{taskId}/events` API into the Shetab Android application, ensuring reliable state transitions, graceful conflict handling, offline resilience, and seamless UI feedback in Persian (RTL).

---

## 2. Scope & Event Scenarios

### 2.1 Base Endpoint
- **HTTP Method:** `POST`
- **Path:** `/study-execution/me/manual-tasks/{taskId}/events`
- **Path Parameter:** `{taskId}` — The UUID string of the manual task received during creation or listing.

### 2.2 Mandatory Request Envelope
All event payloads require four core fields:
1. `clientEventId` (String): A fresh UUID v4 generated on the Android client per user interaction to guarantee idempotency across retries or connection drops.
2. `occurredAt` (String): Exact client-side timestamp formatted in ISO-8601 UTC (e.g., `2026-08-25T10:00:00.000Z`).
3. `type` (String): Event discriminator (`ACTIVITY_STARTED`, `ACTIVITY_PAUSED`, `ACTIVITY_RESUMED`, `ACTIVITY_COMPLETED`, `ACTIVITY_MARKED_DONE`).
4. `expectedSequence` (Int): Sequence counter for optimistic concurrency and event ordering:
   - For initial events (`ACTIVITY_STARTED`, `ACTIVITY_MARKED_DONE`), must be `0`.
   - For subsequent events on an active execution, must strictly match the `eventSequence` received in the previous server response.

### 2.3 Supported Lifecycle Scenarios

1. **Scenario 1: Start Focus Timer (`ACTIVITY_STARTED`)**
   - **Trigger:** User taps "شروع" (Start) button on `FocusTimerScreen`.
   - **`expectedSequence`:** `0`
   - **Payload:**
     ```json
     {
       "clientEventId": "uuid-v4",
       "expectedSequence": 0,
       "type": "ACTIVITY_STARTED",
       "occurredAt": "2026-08-25T10:00:00.000Z"
     }
     ```

2. **Scenario 2: Pause Focus Timer (`ACTIVITY_PAUSED`)**
   - **Trigger:** User taps "توقف موقت" (Pause) button.
   - **`expectedSequence`:** Value of `eventSequence` from the `ACTIVITY_STARTED` (or previous resume) response.
   - **Payload:**
     ```json
     {
       "clientEventId": "uuid-v4",
       "expectedSequence": 1,
       "type": "ACTIVITY_PAUSED",
       "occurredAt": "2026-08-25T10:30:00.000Z"
     }
     ```

3. **Scenario 3: Resume Focus Timer (`ACTIVITY_RESUMED`)**
   - **Trigger:** User taps "ادامه" (Resume) button.
   - **`expectedSequence`:** Value of `eventSequence` from the `ACTIVITY_PAUSED` response.
   - **Payload:**
     ```json
     {
       "clientEventId": "uuid-v4",
       "expectedSequence": 2,
       "type": "ACTIVITY_RESUMED",
       "occurredAt": "2026-08-25T10:45:00.000Z"
     }
     ```

4. **Scenario 4: Full Completion with Timer (`ACTIVITY_COMPLETED` - FULL)**
   - **Trigger:** User finishes session and confirms 100% completion.
   - **`expectedSequence`:** Current tracking sequence from previous response.
   - **Payload:**
     ```json
     {
       "clientEventId": "uuid-v4",
       "expectedSequence": 3,
       "type": "ACTIVITY_COMPLETED",
       "occurredAt": "2026-08-25T11:45:00.000Z",
       "completionOutcome": "FULL",
       "completionPercent": 100
     }
     ```

5. **Scenario 5: Partial Completion with Timer (`ACTIVITY_COMPLETED` - PARTIAL)**
   - **Trigger:** User finishes session but ran out of time or only partially finished (server schedules a backlog for remainder).
   - **`expectedSequence`:** Current tracking sequence from previous response.
   - **Payload:**
     ```json
     {
       "clientEventId": "uuid-v4",
       "expectedSequence": 3,
       "type": "ACTIVITY_COMPLETED",
       "occurredAt": "2026-08-25T11:45:00.000Z",
       "completionOutcome": "PARTIAL",
       "completionPercent": 50,
       "note": "وقت تمام شد و نصف مطالب ماند"
     }
     ```

6. **Scenario 6: Direct Mark as Done (`ACTIVITY_MARKED_DONE`)**
   - **Trigger:** User directly marks task complete from study plan or task list without entering the timer.
   - **`expectedSequence`:** `0`
   - **Payload:**
     ```json
     {
       "clientEventId": "uuid-v4",
       "expectedSequence": 0,
       "type": "ACTIVITY_MARKED_DONE",
       "occurredAt": "2026-08-25T12:00:00.000Z"
     }
     ```

---

## 3. Server Response & Error Handling

### 3.1 Successful Execution Response (`200 OK` / `201 Created`)
```json
{
  "id": "uuid-of-execution",
  "manualTaskId": "uuid-of-manual-task",
  "status": "ACTIVE",
  "eventSequence": 1,
  "actualSeconds": 0,
  "persistedActiveSeconds": 0,
  "timerElapsedSeconds": 0,
  "pausedSeconds": 0,
  "completionPercent": null,
  "startedAt": "2026-08-25T10:00:00.000Z",
  "finishedAt": null
}
```

### 3.2 Error & Conflict Handling
- **`409 Conflict` (`EXECUTION_SEQUENCE_MISMATCH`):** Occurs when the client sequence falls out of sync with the backend. The client will handle this gracefully by refreshing task execution state or reconciling sequence numbers.
- **Network Failures / Timeout:** Idempotency via `clientEventId` allows safe retries without creating duplicated events.

---

## 4. Acceptance Criteria
- [ ] DTOs and API endpoints in `ApiService.kt` accurately reflect the event-driven contract.
- [ ] `FocusTimerViewModel` / Repository maintains `currentSequence` and execution state across lifecycle transitions.
- [ ] Starting timer dispatches `ACTIVITY_STARTED` with `expectedSequence = 0`.
- [ ] Pausing timer dispatches `ACTIVITY_PAUSED` with correct sequence.
- [ ] Resuming timer dispatches `ACTIVITY_RESUMED` with correct sequence.
- [ ] Completing session dispatches `ACTIVITY_COMPLETED` with `FULL` (100%) or `PARTIAL` (1-99% + optional note).
- [ ] Direct completion from study plan card dispatches `ACTIVITY_MARKED_DONE` with `expectedSequence = 0`.
- [ ] UI provides clear Persian feedback, smooth loading indicators, and handles 409 sequence mismatch errors gracefully.
- [ ] Full Unit & Compose tests validating the event sequencing and state transitions.
