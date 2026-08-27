package com.example.domain.usecase

import com.example.data.repository.StudyTaskRepository
import com.example.domain.date.DateTransformer
import com.example.domain.date.JalaliDate
import com.example.network.CreateManualStudyTaskDto
import com.example.network.ManualStudyTaskBodyDto
import com.example.network.NetworkResult
import java.util.UUID

class CreateManualStudyTaskUseCase(
    private val repository: StudyTaskRepository,
) {
    suspend operator fun invoke(
        topicId: String,
        scheduledOnJalali: JalaliDate,
        periodCount: Int,
        minutesPerPeriod: Int,
        customRequestId: String? = null,
    ): NetworkResult<ManualStudyTaskBodyDto> {
        val isoDate = DateTransformer.toGregorianIso(scheduledOnJalali)
        return invoke(
            topicId = topicId,
            scheduledOnIso = isoDate,
            periodCount = periodCount,
            minutesPerPeriod = minutesPerPeriod,
            customRequestId = customRequestId,
        )
    }

    suspend operator fun invoke(
        topicId: String,
        scheduledOnIso: String,
        periodCount: Int,
        minutesPerPeriod: Int,
        customRequestId: String? = null,
    ): NetworkResult<ManualStudyTaskBodyDto> {
        if (topicId.isBlank()) {
            return NetworkResult.Error(400, "لطفاً مبحث مورد نظر را انتخاب کنید")
        }
        if (periodCount !in 1..20) {
            return NetworkResult.Error(400, "تعداد دوره‌ها باید بین ۱ تا ۲۰ باشد")
        }
        if (minutesPerPeriod !in 5..180) {
            return NetworkResult.Error(400, "مدت زمان هر دوره باید بین ۵ تا ۱۸۰ دقیقه باشد")
        }
        if (periodCount * minutesPerPeriod > 1440) {
            return NetworkResult.Error(400, "مجموع زمان مطالعه نمی‌تواند بیش از ۲۴ ساعت در روز باشد")
        }

        val requestId = customRequestId?.ifBlank { null } ?: UUID.randomUUID().toString()
        val request = CreateManualStudyTaskDto(
            requestId = requestId,
            topicId = topicId,
            scheduledOn = scheduledOnIso,
            periodCount = periodCount,
            minutesPerPeriod = minutesPerPeriod,
        )

        return repository.createManualTask(request)
    }
}
