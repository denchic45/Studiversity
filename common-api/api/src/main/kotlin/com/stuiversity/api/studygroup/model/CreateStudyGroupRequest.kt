package com.stuiversity.api.studygroup.model

import com.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CreateStudyGroupRequest(
    val name: String,
    val academicYear: AcademicYear,
    @Serializable(UUIDSerializer::class)
    val specialtyId: UUID?,
    val curatorId: String?
)

@Serializable
data class AcademicYear(val start: Int, val end: Int)