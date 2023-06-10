package com.denchic45.studiversity.data.db.remote.source

import com.denchic45.studiversity.data.db.remote.model.SpecialtyMap
import com.denchic45.studiversity.util.FireMap
import kotlinx.coroutines.flow.Flow

expect class SpecialtyRemoteDataSource {

    suspend fun add(map: FireMap)

    suspend fun remove(specialtyId: String)

    suspend fun update(specialtyMap: FireMap)

    fun findByContainsName(text: String): Flow<List<SpecialtyMap>>

    suspend fun findById(id: String): SpecialtyMap

    fun findAllSpecialties(): Flow<List<SpecialtyMap>>
}