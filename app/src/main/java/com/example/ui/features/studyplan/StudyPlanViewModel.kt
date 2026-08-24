package com.example.ui.features.studyplan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.ApiClient
import com.example.network.CreateManualStudyTaskDto
import com.example.network.DailyStudyTasksBodyDto
import com.example.network.NetworkResult
import com.example.network.StudyExecutionEventDto
import com.example.network.StudyTaskCatalogBodyDto
import com.example.network.StudyTaskDto
import com.example.network.safeApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

enum class StudyTaskFilter {
    ALL,
    IN_PROGRESS,
    PENDING,
    COMPLETED
}

enum class StudyTaskSortOrder {
    DEFAULT,
    DURATION,
    PRIORITY
}

val StudyTaskDto.isCompleted: Boolean
    get() = execution?.status == "COMPLETED"

val StudyTaskDto.isInProgress: Boolean
    get() = execution?.status == "ACTIVE" || execution?.status == "PAUSED" || execution?.status == "AWAITING_COMPLETION"

val StudyTaskDto.isPending: Boolean
    get() = execution == null || execution?.status == "NOT_STARTED"

val StudyTaskDto.elapsedMinutes: Int
    get() = ((execution?.activeSeconds ?: 0) / 60).coerceAtMost(plannedMinutes)

data class StudyPlanUiState(
    val selectedDate: LocalDate = LocalDate.now(ZoneId.of("Asia/Tehran")),
    val catalog: StudyTaskCatalogBodyDto? = null,
    val day: DailyStudyTasksBodyDto? = null,
    val selectedFilter: StudyTaskFilter = StudyTaskFilter.ALL,
    val sortOrder: StudyTaskSortOrder = StudyTaskSortOrder.DEFAULT,
    val bookmarkedIds: Set<String> = emptySet(),
    val loading: Boolean = true,
    val refreshing: Boolean = false,
    val busyTaskId: String? = null,
    val creating: Boolean = false,
    val showAddDialog: Boolean = false,
    val showSortMenu: Boolean = false,
    val error: String? = null,
    val mutationMessage: String? = null,
) {
    val totalTasks: Int
        get() = day?.summary?.total ?: day?.items?.size ?: 0

    val completedTasks: Int
        get() = day?.summary?.completed ?: day?.items?.count { it.isCompleted } ?: 0

    val remainingTasks: Int
        get() = day?.summary?.pending ?: (totalTasks - completedTasks).coerceAtLeast(0)

    val totalStudyMinutes: Int
        get() = day?.items?.sumOf { it.plannedMinutes } ?: 0

    val progressFraction: Float
        get() = if (totalTasks > 0) completedTasks.toFloat() / totalTasks.toFloat() else 0f

    val remainingItems: List<StudyTaskDto>
        get() {
            val list = day?.items?.filter { !it.isCompleted } ?: emptyList()
            return when (sortOrder) {
                StudyTaskSortOrder.DEFAULT -> list
                StudyTaskSortOrder.DURATION -> list.sortedByDescending { it.plannedMinutes }
                StudyTaskSortOrder.PRIORITY -> list.sortedByDescending { it.periodCount }
            }
        }

    val completedItems: List<StudyTaskDto>
        get() = day?.items?.filter { it.isCompleted } ?: emptyList()

    val filteredItems: List<StudyTaskDto>
        get() {
            val base = when (selectedFilter) {
                StudyTaskFilter.ALL -> day?.items ?: emptyList()
                StudyTaskFilter.IN_PROGRESS -> day?.items?.filter { it.isInProgress } ?: emptyList()
                StudyTaskFilter.PENDING -> day?.items?.filter { it.isPending } ?: emptyList()
                StudyTaskFilter.COMPLETED -> day?.items?.filter { it.isCompleted } ?: emptyList()
            }
            return when (sortOrder) {
                StudyTaskSortOrder.DEFAULT -> base
                StudyTaskSortOrder.DURATION -> base.sortedByDescending { it.plannedMinutes }
                StudyTaskSortOrder.PRIORITY -> base.sortedByDescending { it.periodCount }
            }
        }
}

class StudyPlanViewModel(application: Application) : AndroidViewModel(application) {
    private val api = ApiClient.apiService
    private val _state = MutableStateFlow(StudyPlanUiState())
    val state: StateFlow<StudyPlanUiState> = _state.asStateFlow()

    init {
        loadInitial()
    }

    fun retry() = loadInitial(force = true)

    fun selectPreviousDay() {
        _state.update { it.copy(selectedDate = it.selectedDate.minusDays(1)) }
        loadDay()
    }

    fun selectNextDay() {
        _state.update { it.copy(selectedDate = it.selectedDate.plusDays(1)) }
        loadDay()
    }

    fun selectDate(date: LocalDate) {
        if (date == _state.value.selectedDate) return
        _state.update { it.copy(selectedDate = date) }
        loadDay()
    }

    fun setFilter(filter: StudyTaskFilter) {
        _state.update { it.copy(selectedFilter = filter) }
    }

    fun setSortOrder(sortOrder: StudyTaskSortOrder) {
        _state.update { it.copy(sortOrder = sortOrder, showSortMenu = false) }
    }

    fun toggleSortMenu() {
        _state.update { it.copy(showSortMenu = !it.showSortMenu) }
    }

    fun toggleBookmark(taskId: String) {
        _state.update {
            val updated = if (it.bookmarkedIds.contains(taskId)) {
                it.bookmarkedIds - taskId
            } else {
                it.bookmarkedIds + taskId
            }
            it.copy(bookmarkedIds = updated)
        }
    }

    fun openAddDialog() {
        _state.update { it.copy(showAddDialog = true) }
    }

    fun closeAddDialog() {
        _state.update { it.copy(showAddDialog = false) }
    }

    fun refresh() = loadDay(refresh = true)

    fun clearMessage() {
        _state.update { it.copy(mutationMessage = null) }
    }

    fun createManualTask(
        topicId: String,
        periodCount: Int,
        minutesPerPeriod: Int,
        onCreated: () -> Unit,
    ) = viewModelScope.launch {
        if (periodCount !in 1..20 || minutesPerPeriod !in 5..180) {
            _state.update { it.copy(mutationMessage = "تعداد دوره یا زمان مطالعه معتبر نیست") }
            return@launch
        }
        _state.update { it.copy(creating = true, mutationMessage = null) }
        val request = CreateManualStudyTaskDto(
            requestId = UUID.randomUUID().toString(),
            topicId = topicId,
            scheduledOn = _state.value.selectedDate.toString(),
            periodCount = periodCount,
            minutesPerPeriod = minutesPerPeriod,
        )
        when (val result = safeApiCall { api.createManualStudyTask(request) }) {
            is NetworkResult.Success -> {
                _state.update { it.copy(creating = false, showAddDialog = false, mutationMessage = "تسک با موفقیت ساخته شد") }
                onCreated()
                loadDay(refresh = true)
            }
            is NetworkResult.Error -> _state.update {
                it.copy(creating = false, mutationMessage = result.message.ifBlank { "ساخت تسک انجام نشد" })
            }
            is NetworkResult.Exception -> _state.update {
                it.copy(creating = false, mutationMessage = "ارتباط با سرور برقرار نشد")
            }
        }
    }

    fun startTask(task: StudyTaskDto) = viewModelScope.launch {
        if (task.execution != null) return@launch
        submitSingleEvent(task, "ACTIVITY_STARTED", 0)
    }

    fun markTaskDone(task: StudyTaskDto) = viewModelScope.launch {
        val execution = task.execution
        if (execution == null) {
            submitSingleEvent(
                task = task,
                type = "ACTIVITY_MARKED_DONE",
                expectedSequence = 0,
            )
            return@launch
        }
        when (execution.status) {
            "ACTIVE", "PAUSED" -> {
                val stopped = submitEvent(
                    task = task,
                    type = "ACTIVITY_STOPPED",
                    expectedSequence = execution.eventSequence,
                ) ?: return@launch
                submitSingleEvent(
                    task = task,
                    type = "ACTIVITY_COMPLETED",
                    expectedSequence = stopped,
                    completionOutcome = "FULL",
                    completionPercent = 100,
                )
            }
            "AWAITING_COMPLETION" -> submitSingleEvent(
                task = task,
                type = "ACTIVITY_COMPLETED",
                expectedSequence = execution.eventSequence,
                completionOutcome = "FULL",
                completionPercent = 100,
            )
        }
    }

    fun cancelTask(task: StudyTaskDto) = viewModelScope.launch {
        if (task.sourceType != "MANUAL" || task.execution != null) return@launch
        _state.update { it.copy(busyTaskId = task.id, mutationMessage = null) }
        when (val result = safeApiCall { api.cancelManualStudyTask(task.id) }) {
            is NetworkResult.Success -> {
                _state.update { it.copy(busyTaskId = null, mutationMessage = "تسک حذف شد") }
                loadDay(refresh = true)
            }
            is NetworkResult.Error -> _state.update {
                it.copy(busyTaskId = null, mutationMessage = result.message.ifBlank { "حذف تسک انجام نشد" })
            }
            is NetworkResult.Exception -> _state.update {
                it.copy(busyTaskId = null, mutationMessage = "ارتباط با سرور برقرار نشد")
            }
        }
    }

    private fun loadInitial(force: Boolean = false) = viewModelScope.launch {
        if (!force && _state.value.catalog != null && _state.value.day != null) return@launch
        _state.update { it.copy(loading = true, error = null) }
        val catalogResult = safeApiCall { api.getStudyTaskCatalog() }
        if (catalogResult !is NetworkResult.Success) {
            _state.update {
                it.copy(
                    loading = false,
                    error = when (catalogResult) {
                        is NetworkResult.Error -> catalogResult.message.ifBlank { "دریافت کتاب‌ها انجام نشد" }
                        else -> "ارتباط با سرور برقرار نشد"
                    },
                )
            }
            return@launch
        }
        val catalog = catalogResult.data.body
        if (catalog == null) {
            _state.update { it.copy(loading = false, error = "پاسخ فهرست کتاب‌ها نامعتبر است") }
            return@launch
        }
        _state.update { it.copy(catalog = catalog) }
        loadDayInternal(initial = true)
    }

    private fun loadDay(refresh: Boolean = false) = viewModelScope.launch {
        loadDayInternal(initial = false, refresh = refresh)
    }

    private suspend fun loadDayInternal(initial: Boolean, refresh: Boolean = false) {
        _state.update {
            it.copy(
                loading = initial && it.day == null,
                refreshing = refresh || (!initial && it.day != null),
                error = if (it.day == null) null else it.error,
            )
        }
        when (val result = safeApiCall { api.getStudyTasks(_state.value.selectedDate.toString()) }) {
            is NetworkResult.Success -> {
                val body = result.data.body
                if (body == null) {
                    _state.update { it.copy(loading = false, refreshing = false, error = "پاسخ برنامه نامعتبر است") }
                } else {
                    _state.update { it.copy(day = body, loading = false, refreshing = false, error = null) }
                }
            }
            is NetworkResult.Error -> _state.update {
                it.copy(
                    loading = false,
                    refreshing = false,
                    error = result.message.ifBlank { "دریافت برنامه انجام نشد" },
                )
            }
            is NetworkResult.Exception -> _state.update {
                it.copy(loading = false, refreshing = false, error = "ارتباط با سرور برقرار نشد")
            }
        }
    }

    private suspend fun submitSingleEvent(
        task: StudyTaskDto,
        type: String,
        expectedSequence: Int,
        completionOutcome: String? = null,
        completionPercent: Int? = null,
    ) {
        val sequence = submitEvent(task, type, expectedSequence, completionOutcome, completionPercent)
        if (sequence != null) loadDayInternal(initial = false, refresh = true)
    }

    private suspend fun submitEvent(
        task: StudyTaskDto,
        type: String,
        expectedSequence: Int,
        completionOutcome: String? = null,
        completionPercent: Int? = null,
    ): Int? {
        _state.update { it.copy(busyTaskId = task.id, mutationMessage = null) }
        val request = StudyExecutionEventDto(
            clientEventId = UUID.randomUUID().toString(),
            expectedSequence = expectedSequence,
            type = type,
            occurredAt = Instant.now().toString(),
            completionOutcome = completionOutcome,
            completionPercent = completionPercent,
        )
        val result = if (task.sourceType == "MANUAL") {
            safeApiCall { api.submitManualStudyEvent(task.id, request) }
        } else {
            safeApiCall { api.submitGeneratedStudyEvent(task.id, request) }
        }
        return when (result) {
            is NetworkResult.Success -> {
                val body = result.data.body
                _state.update {
                    it.copy(
                        busyTaskId = null,
                        mutationMessage = when (type) {
                            "ACTIVITY_STARTED" -> "مطالعه شروع شد"
                            "ACTIVITY_COMPLETED", "ACTIVITY_MARKED_DONE" -> "آفرین! تسک انجام شد"
                            else -> null
                        },
                    )
                }
                body?.eventSequence
            }
            is NetworkResult.Error -> {
                _state.update {
                    it.copy(busyTaskId = null, mutationMessage = result.message.ifBlank { "ثبت وضعیت انجام نشد" })
                }
                null
            }
            is NetworkResult.Exception -> {
                _state.update { it.copy(busyTaskId = null, mutationMessage = "ارتباط با سرور برقرار نشد") }
                null
            }
        }
    }
}
