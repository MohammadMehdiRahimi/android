package com.example.data.repository

import com.example.network.CreateManualStudyTaskDto
import com.example.network.DailyStudyTasksBodyDto
import com.example.network.ManualStudyTaskBodyDto
import com.example.network.NetworkResult
import com.example.network.StudyExecutionBodyDto
import com.example.network.StudyExecutionEventDto
import com.example.network.StudyTaskCatalogBodyDto
import com.example.network.UpdateManualStudyTaskDto

interface StudyTaskRepository {
    suspend fun getCatalog(forceRefresh: Boolean = false): NetworkResult<StudyTaskCatalogBodyDto>
    suspend fun getDailyTasks(dateIso: String, forceRefresh: Boolean = false): NetworkResult<DailyStudyTasksBodyDto>
    suspend fun createManualTask(request: CreateManualStudyTaskDto): NetworkResult<ManualStudyTaskBodyDto>
    suspend fun updateManualTask(taskId: String, request: UpdateManualStudyTaskDto): NetworkResult<ManualStudyTaskBodyDto>
    suspend fun cancelManualTask(taskId: String): NetworkResult<Unit>
    suspend fun submitStudyEvent(taskId: String, isManual: Boolean, request: StudyExecutionEventDto): NetworkResult<StudyExecutionBodyDto>
    fun invalidateCache()
}
