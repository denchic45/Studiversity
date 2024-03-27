package com.denchic45.studiversity.api.membership.model

import com.denchic45.studiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class MembershipResponse(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val type: String
)
