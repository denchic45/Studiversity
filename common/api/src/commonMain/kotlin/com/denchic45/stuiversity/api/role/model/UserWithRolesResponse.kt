package com.denchic45.stuiversity.api.role.model

import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UserWithRolesResponse(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val firstName: String,
    val surname: String,
    val patronymic: String?,
    val roles: List<Role>
)
