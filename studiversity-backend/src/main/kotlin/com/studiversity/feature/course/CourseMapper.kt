package com.studiversity.feature.course

import com.studiversity.database.table.CourseDao
import com.stuiversity.api.course.model.CourseResponse
import com.studiversity.feature.course.subject.toResponse

fun CourseDao.toResponse(): CourseResponse = CourseResponse(
    id = id.value,
    name = name,
    subject = subject?.toResponse(),
    archived = archived
)