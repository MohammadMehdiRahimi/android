package com.example.domain.usecase

import com.example.data.repository.StudyTaskRepository
import com.example.domain.date.DateTransformer
import com.example.domain.date.JalaliDate
import com.example.network.DailyStudyTasksBodyDto
import com.example.network.NetworkResult

class GetDailyStudyTasksUseCase(
    private val repository: StudyTaskRepository,
) {
    suspend operator fun invoke(
        jalaliDate: JalaliDate,
        forceRefresh: Boolean = false,
    ): NetworkResult<DailyStudyTasksBodyDto> {
        val isoDate = DateTransformer.toGregorianIso(jalaliDate)
        return repository.getDailyTasks(isoDate, forceRefresh)
    }

    suspend operator fun invoke(
        isoDate: String,
        forceRefresh: Boolean = false,
    ): NetworkResult<DailyStudyTasksBodyDto> {
        return repository.getDailyTasks(isoDate, forceRefresh)
    }
}
