package com.denchic45.kts.data.repository

import com.denchic45.kts.UserEntity
import com.denchic45.kts.data.db.local.source.GroupLocalDataSource
import com.denchic45.kts.data.db.local.source.SpecialtyLocalDataSource
import com.denchic45.kts.data.db.local.source.UserLocalDataSource
import com.denchic45.kts.data.db.remote.model.GroupMap
import com.denchic45.kts.data.db.remote.model.UserMap
import com.denchic45.kts.data.mapper.mapToGroupEntity
import com.denchic45.kts.data.mapper.mapToSpecialtyEntity
import com.denchic45.kts.data.mapper.mapsToUserEntities

interface SaveGroupOperation {
    val groupLocalDataSource: GroupLocalDataSource
    val specialtyLocalDataSource: SpecialtyLocalDataSource
    val userLocalDataSource: UserLocalDataSource


    suspend fun saveGroup(groupMap: GroupMap) {
//        groupLocalDataSource.upsert(groupMap.mapToGroupEntity())
//        upsertUsersOfGroup(groupMap)
        val allUsersEntity: List<UserEntity> = groupMap.allUsers
            .map(::UserMap)
            .mapsToUserEntities()
//        userLocalDataSource.upsert(allUsersEntity)
        val availableUsers = allUsersEntity.map(UserEntity::user_id)
//        userLocalDataSource.deleteMissingStudentsByGroup(groupMap.id, availableUsers)
        specialtyLocalDataSource.upsert(groupMap.specialty.mapToSpecialtyEntity())

        groupLocalDataSource.saveGroup(
            groupMap.mapToGroupEntity(),
            allUsersEntity,
            availableUsers,
            groupMap.specialty.mapToSpecialtyEntity()
        )
    }

    suspend fun saveGroups(groupMaps: List<GroupMap>) {
        for (groupDoc in groupMaps) {
            saveGroup(groupDoc)
        }
    }
}