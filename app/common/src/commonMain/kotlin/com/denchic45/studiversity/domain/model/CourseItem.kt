package com.denchic45.studiversity.domain.model

import com.denchic45.studiversity.api.course.model.CourseResponse
import java.util.*

data class CourseItem(
    val id: UUID,
    val name: String,
    val subject: SubjectItem?,
    val archived: Boolean
)

fun CourseResponse.toItem() = CourseItem(
    id = id,
    name = name,
    subject = subject?.toItem(),
    archived = archived
)