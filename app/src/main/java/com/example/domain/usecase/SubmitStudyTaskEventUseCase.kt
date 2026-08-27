package com.example.domain.usecase

import com.example.data.repository.StudyTaskRepository
import com.example.network.NetworkResult
import com.example.network.StudyExecutionBodyDto
import com.example.network.StudyExecutionEventDto
import com.example.network.currentIsoUtcTimestamp
import java.util.UUID

class SubmitStudyTaskEventUseCase(
    private val repository: StudyTaskRepository,
) {
    suspend operator fun invoke(
        taskId: String,
        isManual: Boolean = true,
        request: StudyExecutionEventDto,
    ): NetworkResult<StudyExecutionBodyDto> {
        return repository.submitStudyEvent(taskId, isManual, request)
    }

    suspend fun start(
        taskId: String,
        isManual: Boolean = true,
        expectedSequence: Int = 0,
    ): NetworkResult<StudyExecutionBodyDto> {
        val event = StudyExecutionEventDto(
            clientEventId = UUID.randomUUID().toString(),
            expectedSequence = expectedSequence,
            type = "ACTIVITY_STARTED",
            occurredAt = currentIsoUtcTimestamp(),
        )
        return repository.submitStudyEvent(taskId, isManual, event)
    }

    suspend fun pause(
        taskId: String,
        isManual: Boolean = true,
        expectedSequence: Int,
    ): NetworkResult<StudyExecutionBodyDto> {
        val event = StudyExecutionEventDto(
            clientEventId = UUID.randomUUID().toString(),
            expectedSequence = expectedSequence,
            type = "ACTIVITY_PAUSED",
            occurredAt = currentIsoUtcTimestamp(),
        )
        return repository.submitStudyEvent(taskId, isManual, event)
    }

    suspend fun resume(
        taskId: String,
        isManual: Boolean = true,
        expectedSequence: Int,
    ): NetworkResult<StudyExecutionBodyDto> {
        val event = StudyExecutionEventDto(
            clientEventId = UUID.randomUUID().toString(),
            expectedSequence = expectedSequence,
            type = "ACTIVITY_RESUMED",
            occurredAt = currentIsoUtcTimestamp(),
        )
        return repository.submitStudyEvent(taskId, isManual, event)
    }

    suspend fun completeFull(
        taskId: String,
        isManual: Boolean = true,
        expectedSequence: Int,
    ): NetworkResult<StudyExecutionBodyDto> {
        val event = StudyExecutionEventDto(
            clientEventId = UUID.randomUUID().toString(),
            expectedSequence = expectedSequence,
            type = "ACTIVITY_COMPLETED",
            occurredAt = currentIsoUtcTimestamp(),
            completionOutcome = "FULL",
            completionPercent = 100,
        )
        return repository.submitStudyEvent(taskId, isManual, event)
    }

    suspend fun completePartial(
        taskId: String,
        isManual: Boolean = true,
        expectedSequence: Int,
        completionPercent: Int,
        note: String? = null,
    ): NetworkResult<StudyExecutionBodyDto> {
        val event = StudyExecutionEventDto(
            clientEventId = UUID.randomUUID().toString(),
            expectedSequence = expectedSequence,
            type = "ACTIVITY_COMPLETED",
            occurredAt = currentIsoUtcTimestamp(),
            completionOutcome = "PARTIAL",
            completionPercent = completionPercent.coerceIn(1, 99),
            note = note?.ifBlank { null },
        )
        return repository.submitStudyEvent(taskId, isManual, event)
    }

    suspend fun markDone(
        taskId: String,
        isManual: Boolean = true,
        expectedSequence: Int = 0,
    ): NetworkResult<StudyExecutionBodyDto> {
        val event = StudyExecutionEventDto(
            clientEventId = UUID.randomUUID().toString(),
            expectedSequence = expectedSequence,
            type = "ACTIVITY_MARKED_DONE",
            occurredAt = currentIsoUtcTimestamp(),
        )
        return repository.submitStudyEvent(taskId, isManual, event)
    }
}
