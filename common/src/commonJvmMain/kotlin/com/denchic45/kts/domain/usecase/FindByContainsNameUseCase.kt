package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.domain.model.DomainModel
import com.denchic45.kts.data.repository.FindByContainsName3Repository
import com.denchic45.kts.data.repository.FindByContainsNameRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.error.NetworkError
import com.denchic45.kts.domain.error.NotFound
import com.denchic45.kts.domain.error.SearchError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import kotlinx.coroutines.flow.*

abstract class FindByContainsNameUseCase<T : DomainModel>(
    private val repository: FindByContainsNameRepository<T>
) {
    operator fun invoke(name: String): Flow<Resource<List<T>>> = flow {
        try {
            emitAll(repository.findByContainsName(name).mapLatest { Resource.Success(it) })
        } catch (t: Throwable) {
            emit(Resource.Error(t))
        }
    }
}

abstract class FindByContainsNameUseCase2<T : DomainModel>(
    private val repository: FindByContainsName3Repository<T>
) {
    operator fun invoke(name: String): Flow<Result<List<T>, SearchError>> =
        repository.findByContainsName3(name).map {
            it.andThen { list ->
                if (list.isNotEmpty()) {
                    Ok(list)
                } else {
                    Err(NotFound)
                }
            }
        }
}


