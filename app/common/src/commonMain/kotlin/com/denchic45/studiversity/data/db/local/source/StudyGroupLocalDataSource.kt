package com.denchic45.studiversity.data.db.local.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.denchic45.studiversity.entity.AppDatabase
import com.denchic45.studiversity.entity.StudyGroup
import com.denchic45.studiversity.entity.StudyGroupQueries
import com.denchic45.studiversity.entity.StudyGroupWithSpecialty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
class StudyGroupLocalDataSource(db: AppDatabase) {

    private val queries: StudyGroupQueries = db.studyGroupQueries

    suspend fun upsert(studyGroup: StudyGroup) = withContext(Dispatchers.IO) {
        queries.upsert(studyGroup)
    }

    suspend fun upsert(studyGroups: List<StudyGroup>) = withContext(Dispatchers.IO) {
        queries.transaction {
            studyGroups.forEach { queries.upsert(it) }
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

    fun getNameById(groupId: String): Flow<StudyGroup> {
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
//        allUsersEntity: List<User>,
//        availableStudentIds: List<String>,
//        Specialty: Specialty,
//    ) {
//        withContext(Dispatchers.IO) {
//            db.transaction {
//                queries.upsert(groupEntity)
//                db.UserQueries.apply {
//                    allUsersEntity.forEach {
//                        upsert(it)
//                        deleteMissingStudentsByGroup(groupEntity.group_id, availableStudentIds)
//                    }
//                }
//                db.SpecialtyQueries.upsert(Specialty)
//            }
//        }
//    }
}