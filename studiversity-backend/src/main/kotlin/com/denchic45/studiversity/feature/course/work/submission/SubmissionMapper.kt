package com.denchic45.studiversity.feature.course.work.submission

import com.denchic45.stuiversity.api.course.work.model.CourseWorkType
import com.denchic45.stuiversity.api.course.work.submission.model.Author
import com.denchic45.stuiversity.api.course.work.grade.GradeResponse
import com.denchic45.stuiversity.api.course.work.submission.model.WorkSubmissionContent
import com.denchic45.stuiversity.api.course.work.submission.model.WorkSubmissionResponse
import com.denchic45.studiversity.database.table.SubmissionDao
import com.denchic45.studiversity.database.table.UserDao
import com.denchic45.studiversity.feature.course.work.toResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun SubmissionDao.toResponse() = when (courseWork.type) {
    CourseWorkType.ASSIGNMENT -> WorkSubmissionResponse(
        id = id.value,
        author = author.toAuthor(),
        state = state,
        courseWorkId = courseWork.id.value,
        content = content?.let { Json.decodeFromString(it) } ?: WorkSubmissionContent(emptyList()),
        grade = grade?.toResponse(),
        updatedAt = updateAt
    )
}

fun UserDao.toAuthor() = Author(
    id = id.value,
    firstName = firstName,
    surname = surname,
    avatarUrl = avatarUrl
)