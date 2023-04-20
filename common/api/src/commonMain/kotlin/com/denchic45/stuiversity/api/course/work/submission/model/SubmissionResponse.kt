package com.denchic45.stuiversity.api.course.work.submission.model

import com.denchic45.stuiversity.api.course.element.model.AttachmentHeader
import com.denchic45.stuiversity.api.course.element.model.CourseElementType
import com.denchic45.stuiversity.api.course.work.grade.GradeResponse
import com.denchic45.stuiversity.util.LocalDateTimeSerializer
import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Serializable(SubmissionSerializer::class)
sealed class SubmissionResponse {
    abstract val id: UUID
    abstract val author: Author
    abstract val state: SubmissionState
    abstract val courseWorkId: UUID
    abstract val type: CourseElementType
    abstract val content: SubmissionContent
    abstract val updatedAt: LocalDateTime?
    abstract val grade: GradeResponse?
}

@Serializable
data class WorkSubmissionResponse(
    @Serializable(UUIDSerializer::class)
    override val id: UUID,
    override val author: Author,
    override val state: SubmissionState,
    @Serializable(UUIDSerializer::class)
    override val courseWorkId: UUID,
    override val content: WorkSubmissionContent = WorkSubmissionContent(emptyList()),
    @Serializable(LocalDateTimeSerializer::class)
    override val updatedAt: LocalDateTime?,
    override val grade: GradeResponse? = null,
) : SubmissionResponse() {
    override val type: CourseElementType = CourseElementType.WORK
}

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
    NEW, CREATED, SUBMITTED, CANCELED_BY_AUTHOR;

    companion object {

        fun notSubmitted() = listOf(
            NEW,
            CREATED,
            CANCELED_BY_AUTHOR
        )
    }
}

fun SubmissionResponse.submitted() = state !in SubmissionState.notSubmitted()

@Serializable
data class Author(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val firstName: String,
    val surname: String,
    val avatarUrl: String
) {
    val fullName = "$firstName $surname"
}