package com.denchic45.studiversity.data.db.remote.source

import com.denchic45.studiversity.data.db.remote.model.SpecialtyMap
import com.denchic45.studiversity.util.FireMap
import kotlinx.coroutines.flow.Flow

actual class SpecialtyRemoteDataSource {

    actual suspend fun add(map: FireMap) {
        TODO("Not yet implemented")
    }

    actual suspend fun update(specialtyMap: FireMap) {
        TODO("Not yet implemented")
    }

    actual suspend fun remove(specialtyId: String) {
    }

    actual fun findByContainsName(text: String): Flow<List<SpecialtyMap>> {
        TODO("Not yet implemented")
    }

    actual suspend fun findById(id: String): SpecialtyMap {
        TODO("Not yet implemented")
    }

    actual fun findAllSpecialties(): Flow<List<SpecialtyMap>> {
        TODO("Not yet implemented")
    }
}