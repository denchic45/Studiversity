package com.denchic45.studiversity.api.role.model

import com.denchic45.studiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UserRolesResponse(
    @Serializable(UUIDSerializer::class) val userId: UUID,
    val roles: List<Role>
)
