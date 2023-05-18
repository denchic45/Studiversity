package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.SpecialtyRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.specialty.model.CreateSpecialtyRequest
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class AddSpecialtyUseCase(private val specialtyRepository: SpecialtyRepository) {
    suspend operator fun invoke(request:CreateSpecialtyRequest): Resource<SpecialtyResponse> {
        return specialtyRepository.add(request)
    }
}