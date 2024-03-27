package com.denchic45.studiversity.api.course.model


import com.denchic45.studiversity.api.course.subject.model.SubjectResponse
import com.denchic45.studiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CourseResponse(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val subject: SubjectResponse?,
    val archived: Boolean
)
