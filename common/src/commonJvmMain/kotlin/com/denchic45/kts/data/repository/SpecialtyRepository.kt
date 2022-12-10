package com.denchic45.kts.data.repository

import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.kts.data.db.local.source.SpecialtyLocalDataSource
import com.denchic45.kts.data.db.remote.source.SpecialtyRemoteDataSource
import com.denchic45.kts.data.mapper.*
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.domain.error.SearchError
import com.denchic45.kts.domain.model.Specialty
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class SpecialtyRepository @Inject constructor(
    override val appVersionService: AppVersionService,
    private val coroutineScope: CoroutineScope,
    private val specialtyLocalDataSource: SpecialtyLocalDataSource,
    private val specialtyRemoteDataSource: SpecialtyRemoteDataSource,
    override val networkService: NetworkService,
) : Repository(), FindByContainsNameRepository<Specialty>,FindByContainsName2Repository<Specialty> {

    override fun findByContainsName(text: String): Flow<List<Specialty>> {
        return specialtyRemoteDataSource.findByContainsName(text)
            .map { maps ->
                specialtyLocalDataSource.upsert(maps.mapsToSpecialEntities())
                maps.mapsToDomains()
            }
    }

    override fun findByContainsName2(text: String): Flow<Result<List<Specialty>, SearchError<out Specialty>>> {
        return specialtyRemoteDataSource.findByContainsName(text)
            .map { maps ->
                specialtyLocalDataSource.upsert(maps.mapsToSpecialEntities())
                Ok(maps.mapsToDomains())
            }
    }

    fun observe(id: String): Flow<Specialty?> {
        coroutineScope.launch {
            specialtyLocalDataSource.upsert(
                specialtyRemoteDataSource.findById(id).mapToSpecialtyEntity()
            )
        }
        return specialtyLocalDataSource.observe(id)
            .map { entity -> entity?.toDomain() }
    }

    fun findAllSpecialties(): Flow<List<Specialty>> {
        return specialtyRemoteDataSource.findAllSpecialties()
            .map { it.mapsToDomains() }
    }


    suspend fun add(specialty: Specialty) {
        requireAllowWriteData()
        specialtyRemoteDataSource.add(specialty.toMap())
    }

    suspend fun update(specialty: Specialty) {
        requireAllowWriteData()
        specialtyRemoteDataSource.update(specialty.toMap())
    }

    suspend fun remove(specialty: Specialty) {
        requireAllowWriteData()
        specialtyRemoteDataSource.remove(specialty.id)
    }
}