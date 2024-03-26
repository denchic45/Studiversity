package com.denchic45.stuiversity.api.studygroup.model

import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CreateStudyGroupRequest(
    val name: String,
    val academicYear: AcademicYear,
    @Serializable(UUIDSerializer::class)
    val specialtyId: UUID? = null,
    val curatorId: String? = null
)

@Serializable
data class AcademicYear(val start: Int, val end: Int)