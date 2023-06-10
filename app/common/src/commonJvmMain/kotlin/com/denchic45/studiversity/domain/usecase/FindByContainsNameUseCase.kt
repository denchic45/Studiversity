package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.FindByContainsNameRepository
import com.denchic45.studiversity.domain.Resource
import kotlinx.coroutines.flow.Flow

abstract class FindByContainsNameUseCase<T>(
    private val repository: FindByContainsNameRepository<T>,
) {
     operator fun invoke(name: String): Flow<Resource<List<T>>> {
        return repository.findByContainsName(name)
    }
}


