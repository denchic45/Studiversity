package com.denchic45.kts.data.mapper

import com.denchic45.kts.UserEntity
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.data.remote.model.UserDoc
import com.denchic45.kts.data.remote.model.UserMap
import com.denchic45.kts.domain.model.User
import java.util.*

fun User.domainToEntity() = UserEntity(
    id,
    firstName,
    surname,
    patronymic,
    groupId ?: "",
    role.toString(),
    email,
    photoUrl,
    gender,
    admin,
    generatedAvatar,
    timestamp!!.time
)

fun List<User>.domainsToEntities() = this.map(User::domainToEntity)

fun UserEntity.toUserDomain() = User(
    user_id,
    first_name,
    surname,
    patronymic,
    user_group_id,
    UserRole.valueOf(role),
    email,
    photo_url,
    Date(timestamp),
    gender,
    admin,
    generated_avatar,
)

fun List<UserEntity>.entitiesToUserDomains() = map { }

@Deprecated("")
fun UserDoc.domainToEntity() = UserEntity(
    id,
    firstName,
    surname,
    patronymic,
    groupId ?: "",
    role.toString(),
    email,
    photoUrl,
    gender,
    admin,
    generatedAvatar,
    timestamp!!.time
)

@Deprecated("")
fun List<UserDoc>.docsToEntities() = this.map(UserDoc::domainToEntity)

@Deprecated("")
fun User.toMap() = UserDoc(
    id = id,
    firstName = firstName,
    surname = surname,
    patronymic = patronymic,
    groupId = groupId,
    role = role,
    email = email,
    photoUrl = photoUrl,
    timestamp = timestamp,
    gender = gender,
    generatedAvatar = generatedAvatar,
    admin = admin
)

@Deprecated("")
fun List<User>.domainsToDocs() = this.map(User::toMap)

fun UserDoc.docToUserDomain() = User(
    id = id,
    firstName = firstName,
    surname = surname,
    patronymic = patronymic,
    groupId = groupId,
    role = role,
    email = email,
    photoUrl = photoUrl,
    timestamp = timestamp!!,
    gender = gender,
    generatedAvatar = generatedAvatar,
    admin = admin,
)

fun List<UserDoc>.docsToDomains() = this.map(UserDoc::docToUserDomain)

fun UserMap.mapToUserEntity() = UserEntity(
    user_id = id,
    first_name = firstName,
    surname = surname,
    patronymic = patronymic,
    user_group_id = groupId,
    role = role.toString(),
    email = email,
    timestamp = timestamp.time,
    gender = gender,
    admin = admin,
    photo_url = photoUrl,
    generated_avatar = generatedAvatar,
)

fun List<UserMap>.mapsToUserEntities() = map { it.mapToUserEntity() }

fun UserMap.mapToUser() = User(
    id = id,
    firstName = firstName,
    surname = surname,
    patronymic = patronymic,
    groupId = groupId,
    role = UserRole.valueOf(role),
    email = email,
    photoUrl = photoUrl,
    timestamp = timestamp,
    gender = gender,
    generatedAvatar = generatedAvatar,
    admin = admin
)

fun List<UserMap>.mapsToUsers() = map { it.mapToUser() }

fun User.domainToUserMap() = mapOf<String, Any?>(
    "id" to id,
    "firstName" to firstName,
    "surname" to surname,
    "patronymic" to patronymic,
    "groupId" to groupId,
    "role" to role,
    "email" to email,
    "photoUrl" to photoUrl,
    "timestamp" to timestamp,
    "gender" to gender,
    "generatedAvatar" to generatedAvatar,
    "admin" to admin,
)

fun List<User>.domainsToUserMaps() = map { it.domainToUserMap() }