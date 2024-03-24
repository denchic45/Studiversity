package com.denchic45.stuiversity.api.submission.model

import com.denchic45.stuiversity.api.course.element.model.AttachmentHeader
import com.denchic45.stuiversity.api.course.work.grade.GradeResponse
import com.denchic45.stuiversity.util.LocalDateTimeSerializer
import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Serializable(SubmissionSerializer::class)
sealed interface SubmissionResponse {
    val id: UUID
    val author: SubmissionAuthor
    val state: SubmissionState
    val courseWorkId: UUID
    val content: SubmissionContent
    val updatedAt: LocalDateTime?
    val grade: GradeResponse?
    val late: Boolean
}

@Serializable
data class WorkSubmissionResponse(
    @Serializable(UUIDSerializer::class)
    override val id: UUID,
    override val author: SubmissionAuthor,
    override val state: SubmissionState,
    @Serializable(UUIDSerializer::class)
    override val courseWorkId: UUID,
    override val content: WorkSubmissionContent = WorkSubmissionContent(emptyList()),
    @Serializable(LocalDateTimeSerializer::class)
    override val updatedAt: LocalDateTime?,
    override val grade: GradeResponse? = null,
    override val late: Boolean
) : SubmissionResponse

@Serializable(SubmissionContentSerializer::class)
sealed interface SubmissionContent {
    fun isEmpty(): Boolean
}

@Serializable
data class WorkSubmissionContent(
    val attachments: List<AttachmentHeader>,
) : SubmissionContent {
    override fun isEmpty(): Boolean = attachments.isEmpty()
}

enum class SubmissionState {
    CREATED, SUBMITTED, CANCELED_BY_AUTHOR;

    companion object {

        fun notSubmitted() = listOf(
            CREATED,
            CANCELED_BY_AUTHOR
        )
    }
}

fun SubmissionResponse.submitted() = state !in SubmissionState.notSubmitted()

@Serializable
data class SubmissionAuthor(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val firstName: String,
    val surname: String,
    val avatarUrl: String
) {
    val fullName = "$firstName $surname"
}