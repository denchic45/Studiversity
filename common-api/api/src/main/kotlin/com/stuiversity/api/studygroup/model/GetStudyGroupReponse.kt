package com.stuiversity.api.studygroup.model

import com.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class StudyGroupResponse(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val academicYear: AcademicYear,
    val specialty: SpecialtyResponse?
)

@Serializable
data class SpecialtyResponse(@Serializable(UUIDSerializer::class) val id: UUID, val name: String)