package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.db.local.source.SpecialtyLocalDataSource
import com.denchic45.studiversity.data.fetchResource
import com.denchic45.studiversity.data.fetchResourceFlow
import com.denchic45.studiversity.data.service.NetworkService
import com.denchic45.stuiversity.api.specialty.SpecialtyApi
import com.denchic45.stuiversity.api.specialty.model.CreateSpecialtyRequest
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.stuiversity.api.specialty.model.UpdateSpecialtyRequest
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class SpecialtyRepository(
    private val specialtyLocalDataSource: SpecialtyLocalDataSource,
    override val networkService: NetworkService,
    private val specialtyApi: SpecialtyApi,
) : NetworkServiceOwner,
    FindByContainsNameRepository<SpecialtyResponse> {

    override fun findByContainsName(text: String) = fetchResourceFlow {
        specialtyApi.search(text)
    }

    fun findById(specialtyId: UUID) = fetchResourceFlow {
        specialtyApi.getById(specialtyId)
    }

    suspend fun add(createSpecialtyRequest: CreateSpecialtyRequest) = fetchResource {
        specialtyApi.create(createSpecialtyRequest)
    }

    suspend fun update(
        specialtyId: UUID,
        updateSpecialtyRequest: UpdateSpecialtyRequest
    ) = fetchResource {
        specialtyApi.update(specialtyId, updateSpecialtyRequest)
    }

    suspend fun remove(specialtyId: UUID) = fetchResource {
        specialtyApi.delete(specialtyId)
    }
}