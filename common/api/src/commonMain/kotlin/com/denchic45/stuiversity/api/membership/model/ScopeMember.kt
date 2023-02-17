package com.denchic45.stuiversity.api.membership.model

import com.denchic45.stuiversity.api.role.model.Role
import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ScopeMember(
    @Serializable(UUIDSerializer::class)
    val userId: UUID,
    val firstName: String,
    val surname: String,
    val patronymic: String?,
    @Serializable(UUIDSerializer::class)
    val scopeId: UUID,
    val membershipIds: List<@Serializable(UUIDSerializer::class) UUID>,
    val roles: List<Role>
)