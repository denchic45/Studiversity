package com.studiversity.feature.course.work.submission

import com.denchic45.stuiversity.api.course.work.model.CourseWorkType
import com.denchic45.stuiversity.api.course.work.submission.model.Author
import com.denchic45.stuiversity.api.course.work.submission.model.WorkSubmissionContent
import com.denchic45.stuiversity.api.course.work.submission.model.WorkSubmissionResponse
import com.studiversity.database.table.SubmissionDao
import com.studiversity.database.table.UserDao
import com.studiversity.feature.user.toUserResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun SubmissionDao.toResponse() = when (courseWork.type) {
    CourseWorkType.ASSIGNMENT -> WorkSubmissionResponse(
        id = id.value,
        author = author.toAuthor(),
        state = state,
        courseWorkId = courseWorkId,
        content = content?.let { Json.decodeFromString(it) } ?: WorkSubmissionContent(emptyList()),
        grade = grade?.value,
        gradedBy = grade?.gradedBy,
        doneAt = doneAt,
        updatedAt = updateAt
    )
}

fun UserDao.toAuthor() = Author(
    id = id.value,
    firstName = firstName,
    surname = surname,
    avatarUrl = avatarUrl
)