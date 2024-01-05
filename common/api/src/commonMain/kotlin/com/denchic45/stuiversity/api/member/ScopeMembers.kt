package com.denchic45.stuiversity.api.member

import com.denchic45.stuiversity.api.role.model.UserWithRolesResponse
import com.denchic45.stuiversity.api.user.model.UserResponse
import kotlinx.serialization.Serializable

@Serializable
data class ScopeMembers(
    val users: List<UserResponse>,
    val userRoles: List<UserWithRolesResponse>
)
