package com.denchic45.studiversity.api.membership.model

import com.denchic45.studiversity.api.role.model.Role
import com.denchic45.studiversity.api.user.model.UserResponse
import com.denchic45.studiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ScopeMember(
    val user: UserResponse,
    @Serializable(UUIDSerializer::class)
    val scopeId: UUID,
    val membershipIds: List<@Serializable(UUIDSerializer::class) UUID>,
    val roles: List<Role>
) {
    val fullName: String
        get() = "${user.firstName}  ${user.surname}"
}