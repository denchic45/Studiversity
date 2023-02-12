package com.stuiversity.api.role.model

import com.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UserRolesResponse(
    @Serializable(UUIDSerializer::class) val userId: UUID,
    val roles: List<Role>
)
