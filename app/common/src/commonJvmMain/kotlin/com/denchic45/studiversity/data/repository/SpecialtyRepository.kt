package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.db.local.source.SpecialtyLocalDataSource
import com.denchic45.studiversity.data.fetchResource
import com.denchic45.studiversity.data.fetchResourceFlow
import com.denchic45.studiversity.data.service.NetworkService
import com.denchic45.stuiversity.api.specialty.SpecialtyApi
import com.denchic45.stuiversity.api.specialty.model.CreateSpecialtyRequest
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.stuiversity.api.specialty.model.UpdateSpecialtyRequest
import java.util.UUID
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class SpecialtyRepository @Inject constructor(
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

//    fun observe(id: String): Flow<Specialty?> {
//        coroutineScope.launch {
//            specialtyLocalDataSource.upsert(
//                specialtyRemoteDataSource.findById(id).mapToSpecialtyEntity()
//            )
//        }
//        return specialtyLocalDataSource.observe(id)
//            .map { entity -> entity?.toDomain() }
//    }

//    fun findAllSpecialties(): Flow<List<Specialty>> {
//        return specialtyRemoteDataSource.findAllSpecialties()
//            .map { it.mapsToDomains() }
//    }


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