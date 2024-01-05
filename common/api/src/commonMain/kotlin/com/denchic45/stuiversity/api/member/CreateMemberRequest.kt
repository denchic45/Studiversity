package com.denchic45.stuiversity.api.member

import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CreateMemberRequest(
    @Serializable(UUIDSerializer::class)
    val memberId: UUID,
    val roleIds: List<Long>
)
