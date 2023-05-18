package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.SpecialtyRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.stuiversity.api.specialty.model.UpdateSpecialtyRequest
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class UpdateSpecialtyUseCase(private val specialtyRepository: SpecialtyRepository) {
    suspend operator fun invoke(
        specialtyId: UUID,
        request: UpdateSpecialtyRequest
    ): Resource<SpecialtyResponse> {
        return specialtyRepository.update(specialtyId, request)
    }
}