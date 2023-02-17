package com.denchic45.stuiversity.api.studygroup.model

import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.stuiversity.util.UUIDSerializer
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