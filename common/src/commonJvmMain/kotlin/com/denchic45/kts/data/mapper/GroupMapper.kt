package com.denchic45.kts.data.mapper

import com.denchic45.kts.GetStudentsWithCuratorByGroupId
import com.denchic45.kts.GroupEntity
import com.denchic45.kts.data.local.model.GroupWithCuratorAndSpecialtyEntities
import com.denchic45.kts.data.remote.model.GroupDoc
import com.denchic45.kts.data.remote.model.GroupMap
import com.denchic45.kts.data.remote.model.UserMap
import com.denchic45.kts.domain.model.*
import com.denchic45.kts.util.SearchKeysGenerator

fun List<GetStudentsWithCuratorByGroupId>.toGroupMembers(): GroupMembers {
    return GroupMembers(
        groupId = first().group_id,
        curator = first { it.curator_id == it.user_id }.let { curatorEntity ->
            GroupCurator(
                id = curatorEntity.user_id,
                firstName = curatorEntity.first_name,
                surname = curatorEntity.surname,
                patronymic = curatorEntity.patronymic,
                groupId = curatorEntity.user_group_id,
                photoUrl = curatorEntity.photo_url
            )
        },
        headmanId = first().headman_id,
        students = filterNot { it.curator_id == it.user_id }
            .map {
                GroupStudent(
                    id = it.user_id,
                    firstName = it.first_name,
                    surname = it.surname,
                    patronymic = it.patronymic,
                    groupId = it.group_id,
                    photoUrl = it.photo_url
                )
            }
    )
}

fun GroupWithCuratorAndSpecialtyEntities.entityToUserDomain() = Group(
    id = groupEntity.group_id,
    name = groupEntity.group_name,
    course = groupEntity.course,
    specialty = specialtyEntity.toDomain(),
    curator = curatorEntity.toUserDomain()
)

fun GroupMap.mapToGroup() = Group(
    id = id,
    name = name,
    course = course,
    specialty = specialty.toDomain(),
    curator = UserMap(curator).mapToUser()
)

fun List<GroupMap>.mapsToDomains() = map(GroupMap::mapToGroup)

fun GroupDoc.toGroupHeader() = GroupHeader(
    id = id,
    name = name,
    specialtyId = specialty.id,
)

fun List<GroupDoc>.docsToGroupHeaders() = map(GroupDoc::toGroupHeader)

fun GroupMap.mapToGroupEntity() = GroupEntity(
    group_id = id,
    group_name = name,
    curator_id = curator["id"] as String,
    specialty_id = specialty.id,
    course = course,
    headman_id = headmanId,
    timestamp = timestamp.time
)

fun GroupMap.toGroupHeader() = GroupHeader(
    id = id,
    name = name,
    specialtyId = specialty.id,
)

fun List<GroupMap>.mapsToGroupHeaders() = map { it.toGroupHeader() }

fun Group.domainToMap(): Map<String, Any> {
    val map: MutableMap<String, Any> = HashMap()
    map["id"] = id
    map["name"] = name
    map["course"] = course
    map["specialtyId"] = specialty.id
//    map["timestamp"] = FieldValue.serverTimestamp()
    map["specialty"] = specialty
    map["curator"] = curator
    map["searchKeys"] = SearchKeysGenerator().generateKeys(name)
    return map
}