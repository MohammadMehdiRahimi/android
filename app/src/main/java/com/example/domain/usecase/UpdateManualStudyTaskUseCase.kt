package com.example.domain.usecase

import com.example.data.repository.StudyTaskRepository
import com.example.domain.date.DateTransformer
import com.example.domain.date.JalaliDate
import com.example.network.ManualStudyTaskBodyDto
import com.example.network.NetworkResult
import com.example.network.StudyTaskDto
import com.example.network.UpdateManualStudyTaskDto

class UpdateManualStudyTaskUseCase(
    private val repository: StudyTaskRepository,
) {
    suspend operator fun invoke(
        task: StudyTaskDto,
        topicId: String? = null,
        scheduledOnJalali: JalaliDate? = null,
        periodCount: Int? = null,
        minutesPerPeriod: Int? = null,
    ): NetworkResult<ManualStudyTaskBodyDto> {
        if (task.execution != null) {
            return NetworkResult.Error(
                400,
                "این تسک شروع شده یا به اتمام رسیده است و امکان ویرایش آن وجود ندارد."
            )
        }

        if (periodCount != null && periodCount !in 1..20) {
            return NetworkResult.Error(400, "تعداد دوره‌ها باید بین ۱ تا ۲۰ باشد")
        }

        if (minutesPerPeriod != null && minutesPerPeriod !in 5..180) {
            return NetworkResult.Error(400, "مدت زمان هر دوره باید بین ۵ تا ۱۸۰ دقیقه باشد")
        }

        val scheduledOnIso = scheduledOnJalali?.let { DateTransformer.toGregorianIso(it) }
        val request = UpdateManualStudyTaskDto(
            topicId = topicId,
            scheduledOn = scheduledOnIso,
            periodCount = periodCount,
            minutesPerPeriod = minutesPerPeriod,
        )

        return repository.updateManualTask(task.id, request)
    }
}
