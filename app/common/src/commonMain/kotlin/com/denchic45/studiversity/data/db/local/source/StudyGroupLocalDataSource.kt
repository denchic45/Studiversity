package com.denchic45.studiversity.data.db.local.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.denchic45.studiversity.api.member.ScopeMember
import com.denchic45.studiversity.data.db.local.suspendedTransaction
import com.denchic45.studiversity.entity.AppDatabase
import com.denchic45.studiversity.entity.Specialty
import com.denchic45.studiversity.entity.SpecialtyQueries
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
    private val specialtyQueries: SpecialtyQueries = db.specialtyQueries
    private val f = db.eventMemberQueries

    suspend fun upsert(studyGroup: StudyGroup) = withContext(Dispatchers.IO) {
        queries.upsert(studyGroup)
    }

    suspend fun upsert(studyGroups: List<StudyGroup>) = withContext(Dispatchers.IO) {
        queries.transaction {
            studyGroups.forEach { queries.upsert(it) }
        }
    }

    suspend fun upsert(studyGroup: StudyGroup, specialty: Specialty?) {
        queries.suspendedTransaction {
            specialty?.let(specialtyQueries::upsert)
            queries.upsert(studyGroup)
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

    suspend fun observeMembers(studyGroupId: String) = queries.suspendedTransaction {

    }

    fun upsertMembers(studyGroupId: String, members: List<ScopeMember>) {
        TODO("Not yet implemented")
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