package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SpecialtyRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class FindSpecialtyByIdUseCase(private val specialtyRepository: SpecialtyRepository) {
    operator fun invoke(specialtyId: UUID): Flow<Resource<SpecialtyResponse>> {
        return specialtyRepository.findById(specialtyId)
    }
}