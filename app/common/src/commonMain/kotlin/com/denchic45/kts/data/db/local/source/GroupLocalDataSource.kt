package com.denchic45.kts.data.db.local.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.denchic45.kts.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class GroupLocalDataSource @Inject constructor(private val db: AppDatabase) {

    private val queries: StudyGroupEntityQueries = db.studyGroupEntityQueries

    suspend fun upsert(groupEntity: StudyGroupEntity) = withContext(Dispatchers.IO) {
        queries.upsert(groupEntity)
    }

    suspend fun upsert(groupEntities: List<StudyGroupEntity>) = withContext(Dispatchers.IO) {
        queries.transaction {
            groupEntities.forEach { queries.upsert(it) }
        }
    }

    suspend fun get(id: String): StudyGroupWithSpecialty? = withContext(Dispatchers.IO) {
        queries.getById(id).executeAsOneOrNull()
    }

    fun observe(id: String): Flow<StudyGroupWithSpecialty?> {
        return queries.getById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
    }

    fun getNameById(groupId: String): Flow<StudyGroupEntity> {
        return queries.getNameById(groupId).asFlow().mapToOne(Dispatchers.IO)
    }

    fun observeIsExist(id: String): Flow<Boolean> {
        return queries.isExist(id).asFlow().mapToOne(Dispatchers.IO)
    }


    suspend fun isExist(id: String): Boolean {
        return withContext(Dispatchers.IO) {
            queries.isExist(id).executeAsOne()
        }
    }

    suspend fun deleteById(groupId: String) = withContext(Dispatchers.IO) {
        queries.deleteById(groupId)
    }

//    suspend fun saveGroup(
//        groupEntity: StudyGroupEntity,
//        allUsersEntity: List<UserEntity>,
//        availableStudentIds: List<String>,
//        specialtyEntity: SpecialtyEntity,
//    ) {
//        withContext(Dispatchers.IO) {
//            db.transaction {
//                queries.upsert(groupEntity)
//                db.userEntityQueries.apply {
//                    allUsersEntity.forEach {
//                        upsert(it)
//                        deleteMissingStudentsByGroup(groupEntity.group_id, availableStudentIds)
//                    }
//                }
//                db.specialtyEntityQueries.upsert(specialtyEntity)
//            }
//        }
//    }
}