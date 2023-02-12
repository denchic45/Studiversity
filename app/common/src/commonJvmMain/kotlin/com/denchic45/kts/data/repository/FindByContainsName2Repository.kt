package com.denchic45.kts.data.repository

import com.denchic45.kts.data.domain.model.DomainModel
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.data.service.withCollectHasNetwork
import com.denchic45.kts.data.service.withHasNetwork
import com.denchic45.kts.domain.error.NetworkError
import com.denchic45.kts.domain.error.NotFound
import com.denchic45.kts.domain.error.SearchError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

interface FindByContainsName2Repository<T : DomainModel> {
    fun findByContainsName2(text: String): Flow<Result<List<T>, SearchError>>
}

abstract class FindByContainsNameRepositoryDelegate<T : DomainModel>(
    private val networkService: NetworkService,
    private val search: (text: String) -> Flow<List<T>>
) : FindByContainsName2Repository<T> {
    override fun findByContainsName2(text: String): Flow<Result<List<T>, SearchError>> {
        return if (networkService.isNetworkAvailable) {
            search(text).map { if (it.isEmpty()) Err(NotFound) else Ok(it) }
        } else flowOf(Err(NetworkError))
    }
}

interface FindByContainsName3Repository<T : DomainModel> {

    val networkService: NetworkService

    fun findByContainsName3(text: String): Flow<Result<List<T>, SearchError>>
}

fun <T : DomainModel> FindByContainsName3Repository<T>.findByContainsName(
    searchLambda: () -> List<T>
): Result<List<T>, SearchError> = networkService.withHasNetwork {
    searchLambda().let {
        if (it.isEmpty()) Err(NotFound) else Ok(it)
    }
}

fun <T : DomainModel> FindByContainsName3Repository<T>.observeByContainsName(
    searchLambda: () -> Flow<List<T>>
): Flow<Result<List<T>, SearchError>> = networkService.withCollectHasNetwork {
    searchLambda().map {
       Ok(it)
    }
}