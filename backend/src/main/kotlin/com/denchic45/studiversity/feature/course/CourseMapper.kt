package com.denchic45.studiversity.feature.course

import com.denchic45.studiversity.database.table.CourseDao
import com.denchic45.studiversity.feature.course.subject.toResponse
import com.denchic45.stuiversity.api.course.model.CourseResponse

fun CourseDao.toResponse(): CourseResponse = CourseResponse(
    id = id.value,
    name = name,
    subject = subject?.toResponse(),
    archived = archived
)