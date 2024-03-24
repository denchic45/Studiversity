package com.denchic45.studiversity.feature.course.work.submission

import com.denchic45.studiversity.database.table.SubmissionDao
import com.denchic45.studiversity.database.table.UserDao
import com.denchic45.studiversity.feature.course.work.toResponse
import com.denchic45.stuiversity.api.course.work.model.CourseWorkType
import com.denchic45.stuiversity.api.submission.model.SubmissionAuthor
import com.denchic45.stuiversity.api.submission.model.WorkSubmissionContent
import com.denchic45.stuiversity.api.submission.model.WorkSubmissionResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.LocalTime

fun SubmissionDao.toResponse() = when (courseWork.type) {
    CourseWorkType.ASSIGNMENT -> WorkSubmissionResponse(
        id = id.value,
        submissionAuthor = author.toAuthor(),
        state = state,
        courseWorkId = courseWork.id.value,
        content = content?.let { Json.decodeFromString(it) } ?: WorkSubmissionContent(emptyList()),
        grade = grade?.toResponse(),
        updatedAt = updateAt,
        late = courseWork.dueDate?.let { dueDate ->
            LocalDateTime.of(dueDate, courseWork.dueTime ?: LocalTime.MAX).isBefore(LocalDateTime.now())
        } ?: false
    )
}

fun UserDao.toAuthor() = SubmissionAuthor(
    id = id.value,
    firstName = firstName,
    surname = surname,
    avatarUrl = avatarUrl
)