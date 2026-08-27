package com.example.domain.usecase

import com.example.data.repository.StudyTaskRepository
import com.example.network.NetworkResult
import com.example.network.StudyTaskCatalogBodyDto

class GetStudyCatalogUseCase(
    private val repository: StudyTaskRepository,
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): NetworkResult<StudyTaskCatalogBodyDto> {
        return repository.getCatalog(forceRefresh)
    }
}
