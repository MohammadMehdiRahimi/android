package com.example.data.repository

import com.example.network.ApiService
import com.example.network.CreateManualStudyTaskDto
import com.example.network.DailyStudyTasksBodyDto
import com.example.network.ManualStudyTaskBodyDto
import com.example.network.NetworkResult
import com.example.network.StudyExecutionBodyDto
import com.example.network.StudyExecutionEventDto
import com.example.network.StudyTaskCatalogBodyDto
import com.example.network.UpdateManualStudyTaskDto
import com.example.network.safeApiCall
import java.util.concurrent.ConcurrentHashMap

class StudyTaskRepositoryImpl(
    private val apiService: ApiService,
) : StudyTaskRepository {

    private val tasksByDateCache = ConcurrentHashMap<String, DailyStudyTasksBodyDto>()
    private var catalogCache: StudyTaskCatalogBodyDto? = null
    private var isCacheDirty = false

    override suspend fun getCatalog(forceRefresh: Boolean): NetworkResult<StudyTaskCatalogBodyDto> {
        val cached = catalogCache
        if (!forceRefresh && cached != null && !isCacheDirty) {
            return NetworkResult.Success(cached)
        }

        return when (val result = safeApiCall { apiService.getStudyTaskCatalog() }) {
            is NetworkResult.Success -> {
                val body = result.data.body
                if (body != null) {
                    catalogCache = body
                    NetworkResult.Success(body)
                } else {
                    NetworkResult.Error(500, "اطلاعات کاتالوگ نامعتبر است")
                }
            }
            is NetworkResult.Error -> NetworkResult.Error(result.code, result.message)
            is NetworkResult.Exception -> NetworkResult.Exception(result.throwable)
        }
    }

    override suspend fun getDailyTasks(dateIso: String, forceRefresh: Boolean): NetworkResult<DailyStudyTasksBodyDto> {
        val cached = tasksByDateCache[dateIso]
        if (!forceRefresh && cached != null && !isCacheDirty) {
            return NetworkResult.Success(cached)
        }

        return when (val result = safeApiCall { apiService.getStudyTasks(dateIso) }) {
            is NetworkResult.Success -> {
                val body = result.data.body
                if (body != null) {
                    tasksByDateCache[dateIso] = body
                    isCacheDirty = false
                    NetworkResult.Success(body)
                } else {
                    NetworkResult.Error(500, "اطلاعات وظایف روزانه نامعتبر است")
                }
            }
            is NetworkResult.Error -> NetworkResult.Error(result.code, result.message)
            is NetworkResult.Exception -> NetworkResult.Exception(result.throwable)
        }
    }

    override suspend fun createManualTask(request: CreateManualStudyTaskDto): NetworkResult<ManualStudyTaskBodyDto> {
        return when (val result = safeApiCall { apiService.createManualStudyTask(request) }) {
            is NetworkResult.Success -> {
                val body = result.data.body
                if (body != null) {
                    invalidateCache()
                    NetworkResult.Success(body)
                } else {
                    NetworkResult.Error(500, "پاسخ ثبت تسک نامعتبر است")
                }
            }
            is NetworkResult.Error -> NetworkResult.Error(result.code, result.message)
            is NetworkResult.Exception -> NetworkResult.Exception(result.throwable)
        }
    }

    override suspend fun updateManualTask(taskId: String, request: UpdateManualStudyTaskDto): NetworkResult<ManualStudyTaskBodyDto> {
        return when (val result = safeApiCall { apiService.updateManualStudyTask(taskId, request) }) {
            is NetworkResult.Success -> {
                val body = result.data.body
                if (body != null) {
                    invalidateCache()
                    NetworkResult.Success(body)
                } else {
                    NetworkResult.Error(500, "پاسخ ویرایش تسک نامعتبر است")
                }
            }
            is NetworkResult.Error -> NetworkResult.Error(result.code, result.message)
            is NetworkResult.Exception -> NetworkResult.Exception(result.throwable)
        }
    }

    override suspend fun cancelManualTask(taskId: String): NetworkResult<Unit> {
        return when (val result = safeApiCall { apiService.cancelManualStudyTask(taskId) }) {
            is NetworkResult.Success -> {
                invalidateCache()
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> NetworkResult.Error(result.code, result.message)
            is NetworkResult.Exception -> NetworkResult.Exception(result.throwable)
        }
    }

    override suspend fun submitStudyEvent(
        taskId: String,
        isManual: Boolean,
        request: StudyExecutionEventDto,
    ): NetworkResult<StudyExecutionBodyDto> {
        val primaryCall = if (isManual) {
            safeApiCall { apiService.submitManualStudyEvent(taskId, request) }
        } else {
            safeApiCall { apiService.submitGeneratedStudyEvent(taskId, request) }
        }

        val result = if (primaryCall is NetworkResult.Error && primaryCall.code == 404) {
            // Fallback to the alternative endpoint if first one returns 404
            if (isManual) {
                safeApiCall { apiService.submitGeneratedStudyEvent(taskId, request) }
            } else {
                safeApiCall { apiService.submitManualStudyEvent(taskId, request) }
            }
        } else {
            primaryCall
        }

        return when (result) {
            is NetworkResult.Success -> {
                val body = result.data.body
                if (body != null) {
                    if (request.type == "ACTIVITY_COMPLETED" || request.type == "ACTIVITY_MARKED_DONE") {
                        invalidateCache()
                    }
                    NetworkResult.Success(body)
                } else {
                    NetworkResult.Error(500, "پاسخ ثبت رویداد نامعتبر است")
                }
            }
            is NetworkResult.Error -> NetworkResult.Error(result.code, result.message)
            is NetworkResult.Exception -> NetworkResult.Exception(result.throwable)
        }
    }

    override fun invalidateCache() {
        isCacheDirty = true
        tasksByDateCache.clear()
    }
}
