package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SpecialtyRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.specialty.model.CreateSpecialtyRequest
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import me.tatarka.inject.annotations.Inject

@Inject
class AddSpecialtyUseCase(private val specialtyRepository: SpecialtyRepository) {
    suspend operator fun invoke(request:CreateSpecialtyRequest): Resource<SpecialtyResponse> {
        return specialtyRepository.add(request)
    }
}