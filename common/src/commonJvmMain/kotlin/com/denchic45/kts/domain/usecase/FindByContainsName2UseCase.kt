package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.domain.model.DomainModel
import com.denchic45.kts.data.repository.FindByContainsName2Repository
import com.denchic45.kts.domain.error.SearchError
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow


abstract class FindByContainsName2UseCase<T : DomainModel>(
    private val repository: FindByContainsName2Repository<T>
) {
    operator fun invoke(name: String): Flow<Result<List<T>, SearchError<out T>>> {
        return repository.findByContainsName2(name)
    }
}

