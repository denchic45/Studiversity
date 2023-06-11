package com.denchic45.studiversity.feature.role.mapper

import com.denchic45.studiversity.database.table.RoleDao
import com.denchic45.studiversity.database.table.UserRoleScopeDao
import com.denchic45.stuiversity.api.role.model.Role
import com.denchic45.stuiversity.api.role.model.UserRolesResponse
import com.denchic45.stuiversity.api.role.model.UserWithRolesResponse
import java.util.*

fun RoleDao.toRole(): Role = Role(id = id.value, resource = shortName)

fun Iterable<UserRoleScopeDao>.toUserRolesResponse(userId: UUID): UserRolesResponse = UserRolesResponse(
    userId = userId,
    roles = map { it.role.toRole() }
)

fun Iterable<UserRoleScopeDao>.toUserWithRoles(): UserWithRolesResponse = first().user
    .let { userDao ->
        UserWithRolesResponse(id = userDao.id.value,
            firstName = userDao.firstName,
            surname = userDao.surname,
            patronymic = userDao.patronymic,
            roles = map { it.role.toRole() }
        )
    }

fun Iterable<UserRoleScopeDao>.toUsersWithRoles(): List<UserWithRolesResponse> {
    return groupBy(UserRoleScopeDao::userId)
        .map { (_, userRoleScopeDaoList) ->
            userRoleScopeDaoList.toUserWithRoles()
        }
}