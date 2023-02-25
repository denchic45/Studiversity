package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.SpecialtyRepository
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import javax.inject.Inject

class FindSpecialtyByContainsNameUseCase @Inject constructor(
    specialtyRepository: SpecialtyRepository,
) : FindByContainsNameUseCase<SpecialtyResponse>(specialtyRepository)