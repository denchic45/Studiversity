package com.denchic45.studiversity.ui.coursework

import com.denchic45.stuiversity.api.course.work.grade.GradeResponse
import com.denchic45.stuiversity.api.course.work.submission.model.Author
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionResponse
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import java.time.LocalDateTime
import java.util.*

data class SubmissionUiState(
    val id: UUID,
    val author: Author,
    val grade: GradeResponse?,
    val state: SubmissionState,
    val updatedAt: LocalDateTime?,
    val late: Boolean
) {
    val title: String
        get() = grade?.let {
            "Оценено: ${it.value}/5"
        } ?: when (state) {
            SubmissionState.CREATED,
            -> "Не сдано"

            SubmissionState.SUBMITTED -> "Сдано"
            SubmissionState.CANCELED_BY_AUTHOR -> "Отменено"
        }
}

fun SubmissionResponse.toUiState() = SubmissionUiState(
    id = id,
    author = author,
    grade = grade,
    state = state,
    updatedAt = updatedAt,
    late = late
)