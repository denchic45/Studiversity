package com.denchic45.kts.data.db.local.source

import com.denchic45.kts.*
import com.denchic45.kts.data.db.local.model.GroupWithCuratorAndSpecialtyEntities
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class GroupLocalDataSource @Inject constructor(private val db: AppDatabase) {

    private val queries: GroupEntityQueries = db.groupEntityQueries

    suspend fun upsert(groupEntity: GroupEntity) = withContext(Dispatchers.IO) {
        queries.upsert(groupEntity)
    }

    suspend fun upsert(groupEntities: List<GroupEntity>) = withContext(Dispatchers.IO) {
        queries.transaction {
            groupEntities.forEach { queries.upsert(it) }
        }
    }

    suspend fun get(id: String): GroupWithCuratorAndSpecialtyEntities? =
        withContext(Dispatchers.IO) {
            queries.getWithCuratorAndSpecialtyByCuratorId(id) { group_id, group_name, curator_id, course, specialty_id, headman_id, timestamp, user_id, first_name, surname, patronymic, user_group_id, role, email, photo_url, gender, admin, generated_avatar, timestamp_, specialty_id_, name ->
                GroupWithCuratorAndSpecialtyEntities(
                    GroupEntity(
                        group_id,
                        group_name,
                        curator_id,
                        course.toInt(),
                        specialty_id,
                        headman_id,
                        timestamp
                    ),
                    UserEntity(
                        user_id,
                        first_name,
                        surname,
                        patronymic,
                        user_group_id,
                        role,
                        email,
                        photo_url,
                        gender,
                        admin,
                        generated_avatar,
                        timestamp_
                    ),
                    SpecialtyEntity(specialty_id_, name)
                )
            }.executeAsOneOrNull()
        }

    fun observe(id: String): Flow<GroupWithCuratorAndSpecialtyEntities?> {
        return queries.getWithCuratorAndSpecialtyById(
            id
        ) { group_id, group_name, curator_id, course, specialty_id, headman_id, timestamp, user_id, first_name, surname, patronymic, user_group_id, role, email, photo_url, gender, admin, generated_avatar, timestamp_, specialty_id_, name ->
            GroupWithCuratorAndSpecialtyEntities(
                GroupEntity(
                    group_id,
                    group_name,
                    curator_id,
                    course.toInt(),
                    specialty_id,
                    headman_id,
                    timestamp
                ),
                UserEntity(
                    user_id,
                    first_name,
                    surname,
                    patronymic,
                    user_group_id,
                    role,
                    email,
                    photo_url,
                    gender,
                    admin,
                    generated_avatar,
                    timestamp_
                ),
                SpecialtyEntity(specialty_id_, name)
            )
        }
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
    }

    fun getNameById(groupId: String): Flow<String> {
        return queries.getNameById(groupId).asFlow().mapToOne(Dispatchers.IO)
    }

    fun observeIsExist(id: String): Flow<Boolean> {
        return queries.isExist(id).asFlow().mapToOne(Dispatchers.IO)
    }


    fun isExist(id: String): Boolean {
//        return withContext(Dispatchers.IO) {
        return queries.isExist(id).executeAsOne()
//        }
    }

    suspend fun deleteById(groupId: String) = withContext(Dispatchers.IO) {
        queries.deleteById(groupId)
    }

    fun getByStudentId(userId: String): Flow<GroupWithCuratorAndSpecialtyEntities> {
        return queries.getWithCuratorAndSpecialtyByStudentId(
            userId
        ) { group_id, group_name, curator_id, course, specialty_id, headman_id, timestamp, user_id, first_name, surname, patronymic, user_group_id, role, email, photo_url, gender, admin, generated_avatar, timestamp_, specialty_id_, name ->
            GroupWithCuratorAndSpecialtyEntities(
                GroupEntity(
                    group_id,
                    group_name,
                    curator_id,
                    course.toInt(),
                    specialty_id,
                    headman_id,
                    timestamp
                ),
                UserEntity(
                    user_id,
                    first_name,
                    surname,
                    patronymic,
                    user_group_id,
                    role,
                    email,
                    photo_url,
                    gender,
                    admin,
                    generated_avatar,
                    timestamp_
                ),
                SpecialtyEntity(specialty_id_, name)
            )
        }
            .asFlow()
            .mapToOne(Dispatchers.IO)
    }


    fun observeByCuratorId(userId: String): Flow<GroupWithCuratorAndSpecialtyEntities> {
        return queries.getWithCuratorAndSpecialtyByCuratorId<GroupWithCuratorAndSpecialtyEntities>(
            userId
        ) { group_id, group_name, curator_id, course, specialty_id, headman_id, timestamp, user_id, first_name, surname, patronymic, user_group_id, role, email, photo_url, gender, admin, generated_avatar, timestamp_, specialty_id_, name ->
            GroupWithCuratorAndSpecialtyEntities(
                GroupEntity(
                    group_id,
                    group_name,
                    curator_id,
                    course.toInt(),
                    specialty_id,
                    headman_id,
                    timestamp
                ),
                UserEntity(
                    user_id,
                    first_name,
                    surname,
                    patronymic,
                    user_group_id,
                    role,
                    email,
                    photo_url,
                    gender,
                    admin,
                    generated_avatar,
                    timestamp_
                ),
                SpecialtyEntity(specialty_id_, name)
            )
        }
            .asFlow()
            .mapToOne(Dispatchers.IO)
    }

    fun observeGroupIdByCuratorId(curatorId: String): Flow<String> {
        return queries.getGroupIdByCuratorId(curatorId).asFlow().mapToOne()
    }

    suspend fun saveGroup(
        groupEntity: GroupEntity,
        allUsersEntity: List<UserEntity>,
        availableStudentIds: List<String>,
        specialtyEntity: SpecialtyEntity,
    ) {
        withContext(Dispatchers.IO) {
            db.transaction {
                queries.upsert(groupEntity)
                db.userEntityQueries.apply {
                    allUsersEntity.forEach {
                        upsert(it)
                        deleteMissingStudentsByGroup(groupEntity.group_id, availableStudentIds)
                    }
                }
                db.specialtyEntityQueries.upsert(specialtyEntity)
            }
        }
    }
}