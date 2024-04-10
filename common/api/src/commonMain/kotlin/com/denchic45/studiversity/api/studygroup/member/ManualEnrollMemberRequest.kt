package com.denchic45.studiversity.api.studygroup.member

import com.denchic45.studiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ManualEnrollMemberRequest(
    @Serializable(UUIDSerializer::class)
    val memberId: UUID,
    val roleIds: List<Long>
)
