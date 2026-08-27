package com.example.ui.features.studyplan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.repository.StudyTaskRepository
import com.example.data.repository.StudyTaskRepositoryImpl
import com.example.domain.date.CalendarDayItem
import com.example.domain.date.DateTransformer
import com.example.domain.date.JalaliDate
import com.example.domain.usecase.CreateManualStudyTaskUseCase
import com.example.domain.usecase.DeleteManualStudyTaskUseCase
import com.example.domain.usecase.GetDailyStudyTasksUseCase
import com.example.domain.usecase.GetStudyCatalogUseCase
import com.example.domain.usecase.SubmitStudyTaskEventUseCase
import com.example.domain.usecase.UpdateManualStudyTaskUseCase
import com.example.network.ApiClient
import com.example.network.DailyStudyTasksBodyDto
import com.example.network.NetworkResult
import com.example.network.StudyExecutionEventDto
import com.example.network.StudyTaskCatalogBodyDto
import com.example.network.StudyTaskDto
import com.example.network.currentIsoUtcTimestamp
import com.example.network.safeApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
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

val StudyTaskDto.isEditable: Boolean
    get() = sourceType == "MANUAL" && execution == null

val StudyTaskDto.isDeletable: Boolean
    get() = sourceType == "MANUAL" && execution == null

val StudyTaskDto.elapsedMinutes: Int
    get() = ((execution?.activeSeconds ?: 0) / 60).coerceAtMost(plannedMinutes)

data class StudyPlanUiState(
    val selectedJalaliDate: JalaliDate = DateTransformer.getTodayJalali(),
    val calendarDays: List<CalendarDayItem> = emptyList(),
    val catalog: StudyTaskCatalogBodyDto? = null,
    val day: DailyStudyTasksBodyDto? = null,
    val selectedFilter: StudyTaskFilter = StudyTaskFilter.ALL,
    val sortOrder: StudyTaskSortOrder = StudyTaskSortOrder.DEFAULT,
    val bookmarkedIds: Set<String> = emptySet(),
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val busyTaskId: String? = null,
    val creating: Boolean = false,
    val updating: Boolean = false,
    val showAddDialog: Boolean = false,
    val taskBeingEdited: StudyTaskDto? = null,
    val taskBeingDeleted: StudyTaskDto? = null,
    val showSortMenu: Boolean = false,
    val error: String? = null,
    val mutationMessage: String? = null,
) {
    val selectedDate: LocalDate
        get() = selectedJalaliDate.toGregorian()

    val selectedDateHeaderTitle: String
        get() = DateTransformer.formatHeaderTitle(selectedJalaliDate)

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

/**
 * In-memory Cache for Study Tasks & Catalog.
 */
object StudyPlanDataCache {
    private val tasksByDate = mutableMapOf<String, DailyStudyTasksBodyDto>()
    private var catalogCache: StudyTaskCatalogBodyDto? = null
    private var needsRefresh = false

    @Synchronized
    fun getTasks(date: String): DailyStudyTasksBodyDto? {
        if (needsRefresh) return null
        return tasksByDate[date]
    }

    @Synchronized
    fun putTasks(date: String, tasks: DailyStudyTasksBodyDto) {
        tasksByDate[date] = tasks
        needsRefresh = false
    }

    @Synchronized
    fun getCatalog(): StudyTaskCatalogBodyDto? = catalogCache

    @Synchronized
    fun putCatalog(catalog: StudyTaskCatalogBodyDto) {
        catalogCache = catalog
    }

    @Synchronized
    fun invalidate() {
        needsRefresh = true
        tasksByDate.clear()
    }

    @Synchronized
    fun clear() {
        tasksByDate.clear()
        catalogCache = null
        needsRefresh = false
    }
}

class StudyPlanViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: StudyTaskRepository = StudyTaskRepositoryImpl(
        try {
            ApiClient.apiService
        } catch (e: Exception) {
            ApiClient.init(application)
            ApiClient.apiService
        }
    ),
) : AndroidViewModel(application) {

    private val api = ApiClient.apiService
    private val getCatalogUseCase = GetStudyCatalogUseCase(repository)
    private val getDailyTasksUseCase = GetDailyStudyTasksUseCase(repository)
    private val createManualTaskUseCase = CreateManualStudyTaskUseCase(repository)
    private val updateManualTaskUseCase = UpdateManualStudyTaskUseCase(repository)
    private val deleteManualTaskUseCase = DeleteManualStudyTaskUseCase(repository)
    private val submitStudyTaskEventUseCase = SubmitStudyTaskEventUseCase(repository)

    private val initialJalali = DateTransformer.getTodayJalali()
    private val initialIso = DateTransformer.toGregorianIso(initialJalali)

    private val _state = MutableStateFlow(
        StudyPlanUiState(
            selectedJalaliDate = initialJalali,
            calendarDays = DateTransformer.generateWeekCalendarDays(initialJalali),
            catalog = StudyPlanDataCache.getCatalog(),
            day = StudyPlanDataCache.getTasks(initialIso),
            loading = StudyPlanDataCache.getTasks(initialIso) == null,
        )
    )
    val state: StateFlow<StudyPlanUiState> = _state.asStateFlow()

    init {
        loadInitial()
    }

    fun retry() = loadInitial(force = true)

    fun selectPreviousDay() {
        val newDate = _state.value.selectedJalaliDate.minusDays(1)
        selectJalaliDate(newDate)
    }

    fun selectNextDay() {
        val newDate = _state.value.selectedJalaliDate.plusDays(1)
        selectJalaliDate(newDate)
    }

    fun selectJalaliDate(date: JalaliDate) {
        if (date == _state.value.selectedJalaliDate && _state.value.day != null) return
        val isoDateStr = DateTransformer.toGregorianIso(date)
        val cached = StudyPlanDataCache.getTasks(isoDateStr)
        _state.update {
            it.copy(
                selectedJalaliDate = date,
                calendarDays = DateTransformer.generateWeekCalendarDays(date),
                day = cached,
                loading = cached == null,
                error = null,
            )
        }
        loadDay()
    }

    fun selectDate(date: LocalDate) {
        selectJalaliDate(JalaliDate.fromGregorian(date))
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
        if (_state.value.catalog == null) {
            loadCatalogOnly()
        }
    }

    fun closeAddDialog() {
        _state.update { it.copy(showAddDialog = false) }
    }

    fun openEditDialog(task: StudyTaskDto) {
        if (!task.isEditable) {
            _state.update { it.copy(mutationMessage = "این تسک شروع شده یا سیستمی است و قابل ویرایش نیست.") }
            return
        }
        _state.update { it.copy(taskBeingEdited = task) }
        if (_state.value.catalog == null) {
            loadCatalogOnly()
        }
    }

    fun closeEditDialog() {
        _state.update { it.copy(taskBeingEdited = null) }
    }

    fun openDeleteConfirmDialog(task: StudyTaskDto) {
        if (!task.isDeletable) {
            _state.update { it.copy(mutationMessage = "این تسک شروع شده است و امکان حذف آن وجود ندارد.") }
            return
        }
        _state.update { it.copy(taskBeingDeleted = task) }
    }

    fun closeDeleteConfirmDialog() {
        _state.update { it.copy(taskBeingDeleted = null) }
    }

    fun refresh() = loadDay(refresh = true)

    fun clearMessage() {
        _state.update { it.copy(mutationMessage = null) }
    }

    private fun loadCatalogOnly() = viewModelScope.launch {
        val result = getCatalogUseCase()
        if (result is NetworkResult.Success) {
            StudyPlanDataCache.putCatalog(result.data)
            _state.update { it.copy(catalog = result.data) }
        }
    }

    fun createManualTask(
        topicId: String,
        periodCount: Int,
        minutesPerPeriod: Int,
        scheduledOnJalali: JalaliDate = _state.value.selectedJalaliDate,
        onCreated: () -> Unit = {},
    ) = viewModelScope.launch {
        _state.update { it.copy(creating = true, mutationMessage = null) }

        val result = createManualTaskUseCase(
            topicId = topicId,
            scheduledOnJalali = scheduledOnJalali,
            periodCount = periodCount,
            minutesPerPeriod = minutesPerPeriod,
        )

        when (result) {
            is NetworkResult.Success -> {
                StudyPlanDataCache.invalidate()
                _state.update {
                    it.copy(
                        creating = false,
                        showAddDialog = false,
                        mutationMessage = "تسک با موفقیت ساخته شد",
                    )
                }
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

    fun updateManualTask(
        task: StudyTaskDto,
        topicId: String? = null,
        periodCount: Int? = null,
        minutesPerPeriod: Int? = null,
        scheduledOnJalali: JalaliDate? = null,
        onUpdated: () -> Unit = {},
    ) = viewModelScope.launch {
        _state.update { it.copy(updating = true, mutationMessage = null) }

        val result = updateManualTaskUseCase(
            task = task,
            topicId = topicId,
            scheduledOnJalali = scheduledOnJalali,
            periodCount = periodCount,
            minutesPerPeriod = minutesPerPeriod,
        )

        when (result) {
            is NetworkResult.Success -> {
                StudyPlanDataCache.invalidate()
                _state.update {
                    it.copy(
                        updating = false,
                        taskBeingEdited = null,
                        mutationMessage = "تسک با موفقیت ویرایش شد",
                    )
                }
                onUpdated()
                loadDay(refresh = true)
            }
            is NetworkResult.Error -> _state.update {
                it.copy(updating = false, mutationMessage = result.message.ifBlank { "ویرایش تسک انجام نشد" })
            }
            is NetworkResult.Exception -> _state.update {
                it.copy(updating = false, mutationMessage = "ارتباط با سرور برقرار نشد")
            }
        }
    }

    fun confirmDeleteTask(task: StudyTaskDto) = viewModelScope.launch {
        _state.update { it.copy(busyTaskId = task.id, taskBeingDeleted = null, mutationMessage = null) }

        val result = deleteManualTaskUseCase(task)
        when (result) {
            is NetworkResult.Success<*> -> {
                StudyPlanDataCache.invalidate()
                _state.update { it.copy(busyTaskId = null, mutationMessage = "تسک با موفقیت حذف شد") }
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

    fun cancelTask(task: StudyTaskDto) = confirmDeleteTask(task)

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
        } else {
            submitSingleEvent(
                task = task,
                type = "ACTIVITY_COMPLETED",
                expectedSequence = execution.eventSequence,
                completionOutcome = "FULL",
                completionPercent = 100,
            )
        }
    }

    private fun loadInitial(force: Boolean = false) = viewModelScope.launch {
        val currentIso = DateTransformer.toGregorianIso(_state.value.selectedJalaliDate)
        val cachedCatalog = StudyPlanDataCache.getCatalog()
        val cachedDay = StudyPlanDataCache.getTasks(currentIso)

        if (!force && cachedCatalog != null && cachedDay != null) {
            _state.update {
                it.copy(
                    catalog = cachedCatalog,
                    day = cachedDay,
                    loading = false,
                    error = null,
                )
            }
            return@launch
        }

        if (cachedCatalog == null || force) {
            _state.update { it.copy(loading = it.day == null, error = null) }
            val catalogResult = getCatalogUseCase(force)
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
            val catalog = catalogResult.data
            StudyPlanDataCache.putCatalog(catalog)
            _state.update { it.copy(catalog = catalog) }
        }

        loadDayInternal(initial = true, force = force)
    }

    private fun loadDay(refresh: Boolean = false) = viewModelScope.launch {
        loadDayInternal(initial = false, refresh = refresh)
    }

    private suspend fun loadDayInternal(initial: Boolean, refresh: Boolean = false, force: Boolean = false) {
        val currentJalali = _state.value.selectedJalaliDate
        val isoDateStr = DateTransformer.toGregorianIso(currentJalali)
        val cached = StudyPlanDataCache.getTasks(isoDateStr)

        if (!refresh && !force && cached != null) {
            _state.update {
                it.copy(
                    day = cached,
                    loading = false,
                    refreshing = false,
                    error = null,
                )
            }
            return
        }

        _state.update {
            it.copy(
                loading = (initial || cached == null) && it.day == null,
                refreshing = refresh || (!initial && it.day != null),
                error = if (it.day == null) null else it.error,
            )
        }

        when (val result = getDailyTasksUseCase(currentJalali, forceRefresh = refresh || force)) {
            is NetworkResult.Success -> {
                val body = result.data
                StudyPlanDataCache.putTasks(isoDateStr, body)
                _state.update { it.copy(day = body, loading = false, refreshing = false, error = null) }
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
        if (sequence != null) {
            StudyPlanDataCache.invalidate()
            loadDayInternal(initial = false, refresh = true)
        }
    }

    private suspend fun submitEvent(
        task: StudyTaskDto,
        type: String,
        expectedSequence: Int,
        completionOutcome: String? = null,
        completionPercent: Int? = null,
        note: String? = null,
    ): Int? {
        _state.update { it.copy(busyTaskId = task.id, mutationMessage = null) }
        val request = StudyExecutionEventDto(
            clientEventId = UUID.randomUUID().toString(),
            expectedSequence = expectedSequence,
            type = type,
            occurredAt = currentIsoUtcTimestamp(),
            completionOutcome = completionOutcome,
            completionPercent = completionPercent,
            note = note,
        )
        val isManual = task.sourceType.equals("MANUAL", ignoreCase = true)
        val result = submitStudyTaskEventUseCase(
            taskId = task.id,
            isManual = isManual,
            request = request,
        )
        return when (result) {
            is NetworkResult.Success -> {
                val body = result.data
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
                body.eventSequence
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

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(StudyPlanViewModel::class.java)) {
                    return StudyPlanViewModel(application) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
