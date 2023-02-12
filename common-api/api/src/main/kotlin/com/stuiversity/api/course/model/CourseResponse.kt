package com.stuiversity.api.course.model

import com.stuiversity.api.course.subject.model.SubjectResponse
import com.stuiversity.util.UUIDSerializer
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
