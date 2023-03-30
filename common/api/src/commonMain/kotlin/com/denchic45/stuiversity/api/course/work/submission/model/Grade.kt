package com.denchic45.stuiversity.api.course.work.submission.model

import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class GradeRequest(
    val value: Int,
)


@Serializable
data class Grade(
    val value: Int,
    @Serializable(UUIDSerializer::class)
    val courseId: UUID,
    @Serializable(UUIDSerializer::class)
    val studentId: UUID,
    @Serializable(UUIDSerializer::class)
    val gradedBy: UUID,
    @Serializable(UUIDSerializer::class)
    val submissionId: UUID?
)

@Serializable
data class SubmissionGrade(
    val value: Short,
    @Serializable(UUIDSerializer::class)
    val courseId: UUID,
    @Serializable(UUIDSerializer::class)
    val gradedBy: UUID,
    @Serializable(UUIDSerializer::class)
    val submissionId: UUID
)