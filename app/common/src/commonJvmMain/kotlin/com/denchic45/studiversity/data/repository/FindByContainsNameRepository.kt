package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.service.NetworkService
import com.denchic45.studiversity.data.service.withCollectHasNetwork
import com.denchic45.studiversity.data.service.withHasNetwork
import com.denchic45.studiversity.domain.model.DomainModel
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.toResource
import com.denchic45.stuiversity.api.common.ResponseResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface FindByContainsNameRepository<T> {
    val networkService: NetworkService
    fun findByContainsName(text: String): Flow<Resource<List<T>>>
}

suspend fun <T : DomainModel> FindByContainsNameRepository<T>.findByContainsName(
    searchLambda: suspend () -> ResponseResult<List<T>>,
): Resource<List<T>> = networkService.withHasNetwork {
    searchLambda().toResource()
}

interface ObserveByContainsName3Repository<T : DomainModel> {
    val networkService: NetworkService
    fun observeByContainsName(text: String): Flow<Resource<List<T>>>
}

fun <T : DomainModel> ObserveByContainsName3Repository<T>.observeByContainsName(
    searchLambda: () -> Flow<ResponseResult<List<T>>>,
): Flow<Resource<List<T>>> = networkService.withCollectHasNetwork {
    searchLambda().map { it.toResource() }
}