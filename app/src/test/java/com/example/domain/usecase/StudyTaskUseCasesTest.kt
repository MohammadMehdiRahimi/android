package com.example.domain.usecase

import com.example.data.repository.StudyTaskRepository
import com.example.domain.date.JalaliDate
import com.example.network.CreateManualStudyTaskDto
import com.example.network.DailyStudyTasksBodyDto
import com.example.network.ManualStudyTaskBodyDto
import com.example.network.NetworkResult
import com.example.network.StudyExecutionBodyDto
import com.example.network.StudyExecutionEventDto
import com.example.network.StudyTaskBookDto
import com.example.network.StudyTaskCatalogBodyDto
import com.example.network.StudyTaskDto
import com.example.network.StudyTaskExecutionDto
import com.example.network.StudyTaskNamedRefDto
import com.example.network.UpdateManualStudyTaskDto
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeStudyTaskRepository : StudyTaskRepository {
    var lastCreatedRequest: CreateManualStudyTaskDto? = null
    var lastUpdatedTaskId: String? = null
    var lastUpdatedRequest: UpdateManualStudyTaskDto? = null
    var lastCancelledTaskId: String? = null
    var lastSubmittedTaskId: String? = null
    var lastSubmittedIsManual: Boolean? = null
    var lastSubmittedEvent: StudyExecutionEventDto? = null

    override suspend fun getCatalog(forceRefresh: Boolean): NetworkResult<StudyTaskCatalogBodyDto> {
        return NetworkResult.Success(StudyTaskCatalogBodyDto(books = emptyList()))
    }

    override suspend fun getDailyTasks(dateIso: String, forceRefresh: Boolean): NetworkResult<DailyStudyTasksBodyDto> {
        return NetworkResult.Success(DailyStudyTasksBodyDto(date = dateIso, items = emptyList()))
    }

    override suspend fun createManualTask(request: CreateManualStudyTaskDto): NetworkResult<ManualStudyTaskBodyDto> {
        lastCreatedRequest = request
        return NetworkResult.Success(
            ManualStudyTaskBodyDto(
                id = "mock_manual_id",
                title = "تسک دستی",
                scheduledOn = request.scheduledOn,
                book = StudyTaskNamedRefDto("b1", "زیست"),
                chapter = StudyTaskNamedRefDto("c1", "فصل ۱"),
                topic = StudyTaskNamedRefDto("t1", "مبحث ۱"),
                periodCount = request.periodCount,
                minutesPerPeriod = request.minutesPerPeriod,
                plannedMinutes = request.periodCount * request.minutesPerPeriod,
                status = "PENDING",
            )
        )
    }

    override suspend fun updateManualTask(taskId: String, request: UpdateManualStudyTaskDto): NetworkResult<ManualStudyTaskBodyDto> {
        lastUpdatedTaskId = taskId
        lastUpdatedRequest = request
        return NetworkResult.Success(
            ManualStudyTaskBodyDto(
                id = taskId,
                title = "تسک دستی ویرایش شده",
                scheduledOn = request.scheduledOn ?: "2026-08-25",
                book = StudyTaskNamedRefDto("b1", "زیست"),
                chapter = StudyTaskNamedRefDto("c1", "فصل ۱"),
                topic = StudyTaskNamedRefDto("t1", "مبحث ۱"),
                periodCount = request.periodCount ?: 1,
                minutesPerPeriod = request.minutesPerPeriod ?: 45,
                plannedMinutes = (request.periodCount ?: 1) * (request.minutesPerPeriod ?: 45),
                status = "PENDING",
            )
        )
    }

    override suspend fun cancelManualTask(taskId: String): NetworkResult<Unit> {
        lastCancelledTaskId = taskId
        return NetworkResult.Success(Unit)
    }

    override suspend fun submitStudyEvent(
        taskId: String,
        isManual: Boolean,
        request: StudyExecutionEventDto,
    ): NetworkResult<StudyExecutionBodyDto> {
        lastSubmittedTaskId = taskId
        lastSubmittedIsManual = isManual
        lastSubmittedEvent = request
        return NetworkResult.Success(
            StudyExecutionBodyDto(
                id = "exec_test_id",
                manualTaskId = taskId,
                status = when (request.type) {
                    "ACTIVITY_STARTED", "ACTIVITY_RESUMED" -> "ACTIVE"
                    "ACTIVITY_PAUSED" -> "PAUSED"
                    "ACTIVITY_COMPLETED", "ACTIVITY_MARKED_DONE" -> "COMPLETED"
                    else -> "ACTIVE"
                },
                eventSequence = request.expectedSequence + 1,
                completionPercent = request.completionPercent,
            )
        )
    }

    override fun invalidateCache() {}
}

class StudyTaskUseCasesTest {

    private lateinit var fakeRepository: FakeStudyTaskRepository
    private lateinit var createUseCase: CreateManualStudyTaskUseCase
    private lateinit var updateUseCase: UpdateManualStudyTaskUseCase
    private lateinit var deleteUseCase: DeleteManualStudyTaskUseCase
    private lateinit var submitEventUseCase: SubmitStudyTaskEventUseCase

    @Before
    fun setUp() {
        fakeRepository = FakeStudyTaskRepository()
        createUseCase = CreateManualStudyTaskUseCase(fakeRepository)
        updateUseCase = UpdateManualStudyTaskUseCase(fakeRepository)
        deleteUseCase = DeleteManualStudyTaskUseCase(fakeRepository)
        submitEventUseCase = SubmitStudyTaskEventUseCase(fakeRepository)
    }

    @Test
    fun `test create task generates uuid request id and maps jalali date`() = runBlocking {
        val jalaliDate = JalaliDate(1405, 6, 3)
        val result = createUseCase(
            topicId = "topic_dna",
            scheduledOnJalali = jalaliDate,
            periodCount = 2,
            minutesPerPeriod = 45,
        )

        assertTrue(result is NetworkResult.Success)
        val req = fakeRepository.lastCreatedRequest
        assertTrue(req != null)
        assertEquals("topic_dna", req?.topicId)
        assertEquals("2026-08-25", req?.scheduledOn)
        assertEquals(2, req?.periodCount)
        assertEquals(45, req?.minutesPerPeriod)
        assertTrue(req?.requestId?.isNotBlank() == true)
    }

    @Test
    fun `test create task rejects invalid inputs`() = runBlocking {
        val emptyTopicResult = createUseCase(
            topicId = "",
            scheduledOnJalali = JalaliDate(1405, 6, 3),
            periodCount = 1,
            minutesPerPeriod = 45,
        )
        assertTrue(emptyTopicResult is NetworkResult.Error)

        val invalidPeriodResult = createUseCase(
            topicId = "valid_topic",
            scheduledOnJalali = JalaliDate(1405, 6, 3),
            periodCount = 25, // > 20
            minutesPerPeriod = 45,
        )
        assertTrue(invalidPeriodResult is NetworkResult.Error)
    }

    @Test
    fun `test execution lock prevents editing active or completed tasks`() = runBlocking {
        val activeTask = StudyTaskDto(
            id = "task_active",
            sourceType = "MANUAL",
            title = "تسک فعال",
            book = StudyTaskNamedRefDto("b1", "زیست"),
            chapter = StudyTaskNamedRefDto("c1", "فصل ۱"),
            topic = StudyTaskNamedRefDto("t1", "مبحث ۱"),
            scheduledOn = "2026-08-25",
            periodCount = 1,
            minutesPerPeriod = 45,
            plannedMinutes = 45,
            execution = StudyTaskExecutionDto(id = "exec_1", status = "ACTIVE"),
        )

        val result = updateUseCase(
            task = activeTask,
            periodCount = 2,
            minutesPerPeriod = 60,
        )

        assertTrue(result is NetworkResult.Error)
        assertEquals(null, fakeRepository.lastUpdatedTaskId)
    }

    @Test
    fun `test execution lock prevents deleting active or completed tasks`() = runBlocking {
        val activeTask = StudyTaskDto(
            id = "task_active",
            sourceType = "MANUAL",
            title = "تسک فعال",
            book = StudyTaskNamedRefDto("b1", "زیست"),
            chapter = StudyTaskNamedRefDto("c1", "فصل ۱"),
            topic = StudyTaskNamedRefDto("t1", "مبحث ۱"),
            scheduledOn = "2026-08-25",
            periodCount = 1,
            minutesPerPeriod = 45,
            plannedMinutes = 45,
            execution = StudyTaskExecutionDto(id = "exec_1", status = "ACTIVE"),
        )

        val result = deleteUseCase(activeTask)
        assertTrue(result is NetworkResult.Error)
        assertEquals(null, fakeRepository.lastCancelledTaskId)
    }

    @Test
    fun `test unexecuted manual task can be edited and deleted`() = runBlocking {
        val pendingTask = StudyTaskDto(
            id = "task_pending",
            sourceType = "MANUAL",
            title = "تسک در انتظار",
            book = StudyTaskNamedRefDto("b1", "زیست"),
            chapter = StudyTaskNamedRefDto("c1", "فصل ۱"),
            topic = StudyTaskNamedRefDto("t1", "مبحث ۱"),
            scheduledOn = "2026-08-25",
            periodCount = 1,
            minutesPerPeriod = 45,
            plannedMinutes = 45,
            execution = null,
        )

        val editResult = updateUseCase(
            task = pendingTask,
            periodCount = 3,
            minutesPerPeriod = 60,
        )
        assertTrue(editResult is NetworkResult.Success)
        assertEquals("task_pending", fakeRepository.lastUpdatedTaskId)

        val deleteResult = deleteUseCase(pendingTask)
        assertTrue(deleteResult is NetworkResult.Success)
        assertEquals("task_pending", fakeRepository.lastCancelledTaskId)
    }

    @Test
    fun `test start event sends ACTIVITY_STARTED with expected sequence 0`() = runBlocking {
        val result = submitEventUseCase.start(taskId = "task_1", isManual = true, expectedSequence = 0)
        assertTrue(result is NetworkResult.Success)
        val event = fakeRepository.lastSubmittedEvent
        assertNotNull(event)
        assertEquals("ACTIVITY_STARTED", event?.type)
        assertEquals(0, event?.expectedSequence)
        assertEquals("task_1", fakeRepository.lastSubmittedTaskId)
        assertEquals(true, fakeRepository.lastSubmittedIsManual)
    }

    @Test
    fun `test pause and resume events preserve sequence transitions`() = runBlocking {
        val pauseRes = submitEventUseCase.pause(taskId = "task_1", isManual = true, expectedSequence = 1)
        assertTrue(pauseRes is NetworkResult.Success)
        assertEquals("ACTIVITY_PAUSED", fakeRepository.lastSubmittedEvent?.type)
        assertEquals(1, fakeRepository.lastSubmittedEvent?.expectedSequence)

        val resumeRes = submitEventUseCase.resume(taskId = "task_1", isManual = true, expectedSequence = 2)
        assertTrue(resumeRes is NetworkResult.Success)
        assertEquals("ACTIVITY_RESUMED", fakeRepository.lastSubmittedEvent?.type)
        assertEquals(2, fakeRepository.lastSubmittedEvent?.expectedSequence)
    }

    @Test
    fun `test complete full sends 100 percent completion`() = runBlocking {
        val result = submitEventUseCase.completeFull(taskId = "task_1", isManual = true, expectedSequence = 3)
        assertTrue(result is NetworkResult.Success)
        val event = fakeRepository.lastSubmittedEvent
        assertNotNull(event)
        assertEquals("ACTIVITY_COMPLETED", event?.type)
        assertEquals("FULL", event?.completionOutcome)
        assertEquals(100, event?.completionPercent)
        assertEquals(3, event?.expectedSequence)
    }

    @Test
    fun `test complete partial sends clamped percent and note`() = runBlocking {
        val result = submitEventUseCase.completePartial(
            taskId = "task_1",
            isManual = true,
            expectedSequence = 3,
            completionPercent = 65,
            note = "نصف فصل خوانده شد",
        )
        assertTrue(result is NetworkResult.Success)
        val event = fakeRepository.lastSubmittedEvent
        assertNotNull(event)
        assertEquals("ACTIVITY_COMPLETED", event?.type)
        assertEquals("PARTIAL", event?.completionOutcome)
        assertEquals(65, event?.completionPercent)
        assertEquals("نصف فصل خوانده شد", event?.note)
    }

    @Test
    fun `test direct mark done sends ACTIVITY_MARKED_DONE`() = runBlocking {
        val result = submitEventUseCase.markDone(taskId = "task_1", isManual = true, expectedSequence = 0)
        assertTrue(result is NetworkResult.Success)
        val event = fakeRepository.lastSubmittedEvent
        assertNotNull(event)
        assertEquals("ACTIVITY_MARKED_DONE", event?.type)
        assertEquals(0, event?.expectedSequence)
    }
}
