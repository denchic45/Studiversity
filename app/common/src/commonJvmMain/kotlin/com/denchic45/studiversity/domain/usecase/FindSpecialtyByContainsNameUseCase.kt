package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SpecialtyRepository
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import me.tatarka.inject.annotations.Inject

@Inject
class FindSpecialtyByContainsNameUseCase(
    specialtyRepository: SpecialtyRepository,
) : FindByContainsNameUseCase<SpecialtyResponse>(specialtyRepository)