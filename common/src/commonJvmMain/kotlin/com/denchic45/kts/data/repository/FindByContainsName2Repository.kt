package com.denchic45.kts.data.repository

import com.denchic45.kts.data.domain.model.DomainModel
import com.denchic45.kts.domain.error.SearchError
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow

interface FindByContainsName2Repository<T : DomainModel> {
    fun findByContainsName2(text: String): Flow<Result<List<T>, SearchError<out T>>>
}