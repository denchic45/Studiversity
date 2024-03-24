package com.denchic45.stuiversity.api.course.work.grade

import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class GradeRequest(
    val value: Int,
)


@Serializable
data class GradeResponse(
    val value: Int,
    @Serializable(UUIDSerializer::class)
    val courseId: UUID,
    @Serializable(UUIDSerializer::class)
    val studentId: UUID,
    @Serializable(UUIDSerializer::class)
    val gradedBy: UUID?,
    @Serializable(UUIDSerializer::class)
    val submissionId: UUID?
)

@Serializable
data class SubmissionGradeRequest(
    val value: Int,
    @Serializable(UUIDSerializer::class)
    val submissionId: UUID,
    @Serializable(UUIDSerializer::class)
    val gradedBy: UUID
)