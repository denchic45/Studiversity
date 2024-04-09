package com.denchic45.studiversity.api.member

import com.denchic45.studiversity.api.role.model.Role
import com.denchic45.studiversity.api.user.model.UserResponse
import kotlinx.serialization.Serializable

@Serializable
data class ScopeMember(
    val user: UserResponse,
    val roles: List<Role>
)