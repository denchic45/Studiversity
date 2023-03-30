package com.denchic45.stuiversity.api.course.work.submission.model

import com.denchic45.stuiversity.api.course.element.model.AttachmentHeader
import com.denchic45.stuiversity.api.course.element.model.CourseElementType
import com.denchic45.stuiversity.util.LocalDateTimeSerializer
import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Serializable(SubmissionSerializer::class)
sealed class SubmissionResponse {
    abstract val id: UUID
    abstract val authorId: UUID
    abstract val state: SubmissionState
    abstract val courseWorkId: UUID
    abstract val type: CourseElementType
    abstract val content: SubmissionContent?
    abstract val doneAt:LocalDateTime?
    abstract val updatedAt:LocalDateTime?
    abstract val grade: Int?
    abstract val gradedBy: UUID?
}

@Serializable
data class WorkSubmissionResponse(
    @Serializable(UUIDSerializer::class)
    override val id: UUID,
    @Serializable(UUIDSerializer::class)
    override val authorId: UUID,
    override val state: SubmissionState,
    @Serializable(UUIDSerializer::class)
    override val courseWorkId: UUID,
    override val content: WorkSubmissionContent = WorkSubmissionContent(emptyList()),
    @Serializable(LocalDateTimeSerializer::class)
    override val doneAt: LocalDateTime?,
    @Serializable(LocalDateTimeSerializer::class)
    override val updatedAt: LocalDateTime?,
    override val grade: Int? = null,
    @Serializable(UUIDSerializer::class)
    override val gradedBy: UUID? = null,
) : SubmissionResponse() {
    override val type: CourseElementType = CourseElementType.WORK
}

@Serializable(SubmissionContentSerializer::class)
sealed interface SubmissionContent {
    fun isEmpty():Boolean
}

@Serializable
data class WorkSubmissionContent(
    val attachments: List<AttachmentHeader>,
) : SubmissionContent {
    override fun isEmpty(): Boolean = attachments.isEmpty()
}

enum class SubmissionState {
    NEW, CREATED, SUBMITTED,CANCELED_BY_AUTHOR;

    companion object {
        fun notSubmitted() = listOf(
            NEW,
            CREATED,
            CANCELED_BY_AUTHOR
        )
    }
}