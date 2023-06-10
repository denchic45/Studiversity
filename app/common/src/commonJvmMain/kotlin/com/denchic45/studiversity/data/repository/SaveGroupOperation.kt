package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.UserEntity
import com.denchic45.studiversity.data.db.local.source.GroupLocalDataSource
import com.denchic45.studiversity.data.db.local.source.SpecialtyLocalDataSource
import com.denchic45.studiversity.data.db.local.source.UserLocalDataSource
import com.denchic45.studiversity.data.db.remote.model.GroupMap

interface SaveGroupOperation {
    val groupLocalDataSource: GroupLocalDataSource
    val specialtyLocalDataSource: SpecialtyLocalDataSource
    val userLocalDataSource: UserLocalDataSource


    suspend fun saveGroup(groupMap: GroupMap) {
//        val allUsersEntity: List<UserEntity> = groupMap.allUsers
//            .map(::UserMap)
//            .mapsToUserEntities()
//        val availableUsers = allUsersEntity.map(UserEntity::user_id)
//        specialtyLocalDataSource.upsert(groupMap.specialty.mapToSpecialtyEntity())
//
//        groupLocalDataSource.saveGroup(
//            groupMap.mapToGroupEntity(),
//            allUsersEntity,
//            availableUsers,
//            groupMap.specialty.mapToSpecialtyEntity()
//        )
    }

    suspend fun saveGroups(groupMaps: List<GroupMap>) {
        for (groupDoc in groupMaps) {
            saveGroup(groupDoc)
        }
    }
}