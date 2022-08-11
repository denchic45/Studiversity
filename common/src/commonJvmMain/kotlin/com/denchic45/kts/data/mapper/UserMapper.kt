package com.denchic45.kts.data.mapper

import com.denchic45.kts.UserEntity
import com.denchic45.kts.data.db.remote.model.UserMap
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.data.remote.model.UserDoc
import com.denchic45.kts.domain.model.User
import com.denchic45.kts.util.MutableFireMap
import com.denchic45.kts.util.SearchKeysGenerator
import java.util.*

fun User.domainToEntity() = UserEntity(
    user_id = id,
    first_name = firstName,
    surname = surname,
    patronymic = patronymic,
    user_group_id = groupId ?: "",
    role = role.toString(),
    email = email,
    photo_url = photoUrl,
    gender = gender,
    admin = admin,
    generated_avatar = generatedAvatar,
    timestamp = timestamp!!.time
)

fun List<User>.domainsToEntities() = this.map(User::domainToEntity)

fun UserEntity.toUserDomain() = User(
    id = user_id,
    firstName = first_name,
    surname = surname,
    patronymic = patronymic,
    groupId = user_group_id,
    role = UserRole.valueOf(role),
    email = email,
    photoUrl = photo_url,
    timestamp = Date(timestamp),
    gender = gender,
    generatedAvatar = generated_avatar,
    admin = admin
)

fun List<UserEntity>.entitiesToUserDomains() = map { }

@Deprecated("")
fun UserDoc.domainToEntity() = UserEntity(
    user_id = id,
    first_name = firstName,
    surname = surname,
    patronymic = patronymic,
    user_group_id = groupId ?: "",
    role = role.toString(),
    email = email,
    photo_url = photoUrl,
    gender = gender,
    admin = admin,
    generated_avatar = generatedAvatar,
    timestamp = timestamp!!.time
)

@Deprecated("")
fun List<UserDoc>.docsToEntities() = this.map(UserDoc::domainToEntity)

fun User.toMap(): MutableFireMap = mutableMapOf(
    "id" to id,
    "firstName" to firstName,
    "surname" to surname,
    "patronymic" to patronymic,
    "groupId" to groupId,
    "role" to role.toString(),
    "email" to email,
    "photoUrl" to photoUrl,
    "timestamp" to timestamp,
    "gender" to gender,
    "generatedAvatar" to generatedAvatar,
    "admin" to admin,
    "searchKeys" to SearchKeysGenerator().generateKeys(fullName)
)

fun List<User>.domainsToMaps() = this.map(User::toMap)

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
    role = role,
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