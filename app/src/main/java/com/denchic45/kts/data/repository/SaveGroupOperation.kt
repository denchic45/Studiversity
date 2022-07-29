package com.denchic45.kts.data.repository

import com.denchic45.kts.UserEntity
import com.denchic45.kts.data.database.DataBase
import com.denchic45.kts.data.local.db.GroupLocalDataSource
import com.denchic45.kts.data.local.db.SpecialtyLocalDataSource
import com.denchic45.kts.data.local.db.UserLocalDataSource
import com.denchic45.kts.data.mapper.mapToGroupEntity
import com.denchic45.kts.data.mapper.mapToSpecialtyEntity
import com.denchic45.kts.data.mapper.mapsToUserEntities
import com.denchic45.kts.data.remote.model.GroupMap
import com.denchic45.kts.data.remote.model.UserMap

interface SaveGroupOperation {
    val groupLocalDataSource: GroupLocalDataSource
    val specialtyLocalDataSource: SpecialtyLocalDataSource
    val userLocalDataSource: UserLocalDataSource
    val dataBase: DataBase

    private suspend fun upsertUsersOfGroup(groupMap: GroupMap) {
        val allUsersEntity: List<UserEntity> = groupMap.allUsers
            .map(::UserMap)
            .mapsToUserEntities()
        userLocalDataSource.upsert(allUsersEntity)
        val availableUsers = allUsersEntity.map(UserEntity::user_id)
        userLocalDataSource.deleteMissingStudentsByGroup(groupMap.id, availableUsers)
    }

    suspend fun saveGroup(groupMap: GroupMap) {
        groupLocalDataSource.upsert(groupMap.mapToGroupEntity())
        upsertUsersOfGroup(groupMap)
        specialtyLocalDataSource.upsert(groupMap.specialty.mapToSpecialtyEntity())
    }

    suspend fun saveGroups(groupMaps: List<GroupMap>) {
        for (groupDoc in groupMaps) {
            saveGroup(groupDoc)
        }
    }
}