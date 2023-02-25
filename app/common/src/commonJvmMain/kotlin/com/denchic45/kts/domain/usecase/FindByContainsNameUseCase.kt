package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.FindByContainsNameRepository
import com.denchic45.kts.domain.Resource

abstract class FindByContainsNameUseCase<T>(
    private val repository: FindByContainsNameRepository<T>,
) {
    suspend operator fun invoke(name: String): Resource<List<T>> {
        return repository.findByContainsName(name)
    }
}


