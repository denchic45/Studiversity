package com.denchic45.studiversity.api.course.member

import com.denchic45.studiversity.api.role.model.UserWithRolesResponse
import com.denchic45.studiversity.api.user.model.UserResponse
import kotlinx.serialization.Serializable

@Serializable
data class CourseMembers(
    val users: List<UserResponse>,
    val userRoles: List<UserWithRolesResponse>
)
