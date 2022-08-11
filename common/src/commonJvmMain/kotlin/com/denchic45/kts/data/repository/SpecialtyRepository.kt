package com.denchic45.kts.data.repository

import com.denchic45.kts.data.db.local.source.SpecialtyLocalDataSource
import com.denchic45.kts.data.db.remote.source.SpecialtyRemoteDataSource
import com.denchic45.kts.data.mapper.*
import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.di.modules.IoDispatcher
import com.denchic45.kts.domain.model.Specialty
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class SpecialtyRepository @Inject constructor(
    override val appVersionService: AppVersionService,
    private val coroutineScope: CoroutineScope,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val specialtyLocalDataSource: SpecialtyLocalDataSource,
    private val specialtyRemoteDataSource: SpecialtyRemoteDataSource,
    override val networkService: NetworkService,
) : Repository(), FindByContainsNameRepository<Specialty> {

    override fun findByContainsName(text: String): Flow<List<Specialty>> {
        return specialtyRemoteDataSource.findByContainsName(text)
            .map { maps ->
                specialtyLocalDataSource.upsert(maps.mapsToSpecialEntities())
                maps.mapsToDomains()
            }
    }

    fun observe(id: String): Flow<Specialty?> {
        coroutineScope.launch(dispatcher) {
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