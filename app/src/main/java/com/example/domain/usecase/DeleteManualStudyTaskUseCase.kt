package com.example.domain.usecase

import com.example.data.repository.StudyTaskRepository
import com.example.network.NetworkResult
import com.example.network.StudyTaskDto

class DeleteManualStudyTaskUseCase(
    private val repository: StudyTaskRepository,
) {
    suspend operator fun invoke(task: StudyTaskDto): NetworkResult<Unit> {
        val execution = task.execution
        if (execution != null && (execution.status == "ACTIVE" || execution.status == "COMPLETED" || execution.activeSeconds > 0)) {
            return NetworkResult.Error(
                400,
                "این تسک شروع شده یا به اتمام رسیده است و امکان لغو آن وجود ندارد."
            )
        }
        if (!task.sourceType.equals("MANUAL", ignoreCase = true)) {
            return NetworkResult.Error(
                400,
                "تنها تسک‌های دستی امکان حذف شدن دارند."
            )
        }
        val result = repository.cancelManualTask(task.id)
        if (result is NetworkResult.Error && result.code == 404 && !task.sourceId.isNullOrBlank() && task.sourceId != task.id) {
            return repository.cancelManualTask(task.sourceId)
        }
        return result
    }
}
