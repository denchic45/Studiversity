package com.denchic45.stuiversity.api.membership.model

import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CreateMembershipRequest(
    val type: String,
    @Serializable(UUIDSerializer::class)
    val scopeId: UUID,
    val details: MembershipDetails? = null
)

sealed interface MembershipDetails

@Serializable
data class StudyGroupExternalMembershipDetails(
    @Serializable(UUIDSerializer::class)
    val studyGroupId: UUID
) : MembershipDetails