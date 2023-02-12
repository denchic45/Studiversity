package com.studiversity.feature.course.work.submission

import com.studiversity.database.table.SubmissionDao
import com.stuiversity.api.course.work.model.CourseWorkType
import com.stuiversity.api.course.work.submission.model.AssignmentSubmissionResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun SubmissionDao.toResponse() = when (courseWork.type) {
    CourseWorkType.ASSIGNMENT -> AssignmentSubmissionResponse(
        id = id.value,
        authorId = authorId,
        state = state,
        courseWorkId = courseWorkId,
        content = content?.let { Json.decodeFromString(it) },
        grade = grade?.value,
        gradedBy = grade?.gradedBy
    )
}