package com.denchic45.studiversity.feature.course

import com.denchic45.studiversity.database.table.CourseDao
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.studiversity.feature.course.subject.toResponse

fun CourseDao.toResponse(): CourseResponse = CourseResponse(
    id = id.value,
    name = name,
    subject = subject?.toResponse(),
    archived = archived
)