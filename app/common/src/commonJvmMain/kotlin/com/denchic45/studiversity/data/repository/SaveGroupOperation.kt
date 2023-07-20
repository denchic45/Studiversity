package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.db.local.source.SpecialtyLocalDataSource
import com.denchic45.studiversity.data.db.local.source.StudyGroupLocalDataSource
import com.denchic45.studiversity.data.db.local.source.UserLocalDataSource
import com.denchic45.studiversity.data.db.remote.model.GroupMap

interface SaveGroupOperation {
    val studyGroupLocalDataSource: StudyGroupLocalDataSource
    val specialtyLocalDataSource: SpecialtyLocalDataSource
    val userLocalDataSource: UserLocalDataSource


    suspend fun saveGroup(groupMap: GroupMap) {
//        val allUsersEntity: List<User> = groupMap.allUsers
//            .map(::UserMap)
//            .mapsToUserEntities()
//        val availableUsers = allUsersEntity.map(User::user_id)
//        specialtyLocalDataSource.upsert(groupMap.specialty.mapToSpecialty())
//
//        groupLocalDataSource.saveGroup(
//            groupMap.mapToGroupEntity(),
//            allUsersEntity,
//            availableUsers,
//            groupMap.specialty.mapToSpecialty()
//        )
    }

    suspend fun saveGroups(groupMaps: List<GroupMap>) {
        for (groupDoc in groupMaps) {
            saveGroup(groupDoc)
        }
    }
}