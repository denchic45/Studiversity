package com.denchic45.studiversity.api.member

import com.denchic45.studiversity.api.role.model.Role
import com.denchic45.studiversity.api.role.model.UserWithRolesResponse
import com.denchic45.studiversity.api.user.model.UserResponse
import kotlinx.serialization.Serializable

@Serializable
data class ScopeMembers(
    val users: List<UserResponse>,
    val userRoles: List<UserWithRolesResponse>
)

@Serializable
data class ScopeMember(
    val user: UserResponse,
    val roles: List<Role>
)