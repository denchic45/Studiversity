package com.denchic45.studiversity.ui.coursework

import com.denchic45.studiversity.ui.model.AttachmentItem
import com.denchic45.stuiversity.api.course.work.grade.GradeResponse
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionResponse
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import java.time.LocalDateTime
import java.util.UUID

data class SubmissionUiState(
    val id: UUID,
    val attachments: List<AttachmentItem>,
    val grade: GradeResponse?,
    val state: SubmissionState,
    val updatedAt: LocalDateTime?
)

fun SubmissionResponse.toUiState(attachmentItems: List<AttachmentItem>): SubmissionUiState {
    return SubmissionUiState(
        id = id,
        attachments = attachmentItems,
        grade = grade,
        state = state,
        updatedAt = updatedAt
    )
}