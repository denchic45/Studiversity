package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SpecialtyRepository
import com.denchic45.studiversity.domain.Resource
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