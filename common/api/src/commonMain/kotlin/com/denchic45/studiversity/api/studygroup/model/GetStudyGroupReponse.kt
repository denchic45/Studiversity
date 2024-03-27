package com.denchic45.studiversity.api.studygroup.model

import com.denchic45.studiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.studiversity.util.UUIDSerializer
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