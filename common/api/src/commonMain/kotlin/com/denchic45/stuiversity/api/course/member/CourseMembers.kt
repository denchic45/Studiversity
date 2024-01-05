package com.denchic45.stuiversity.api.course.member

import com.denchic45.stuiversity.api.role.model.UserWithRolesResponse
import com.denchic45.stuiversity.api.user.model.UserResponse
import kotlinx.serialization.Serializable

@Serializable
data class CourseMembers(
    val users: List<UserResponse>,
    val userRoles: List<UserWithRolesResponse>
)
