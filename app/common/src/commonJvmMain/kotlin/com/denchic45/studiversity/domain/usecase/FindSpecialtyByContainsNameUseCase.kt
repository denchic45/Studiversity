package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.SpecialtyRepository
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class FindSpecialtyByContainsNameUseCase @Inject constructor(
    specialtyRepository: SpecialtyRepository,
) : FindByContainsNameUseCase<SpecialtyResponse>(specialtyRepository)