package com.denchic45.stuiversity.api.membership.model

import com.denchic45.stuiversity.api.role.model.Role
import com.denchic45.stuiversity.api.user.model.UserResponse
import com.denchic45.stuiversity.util.UUIDSerializer
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