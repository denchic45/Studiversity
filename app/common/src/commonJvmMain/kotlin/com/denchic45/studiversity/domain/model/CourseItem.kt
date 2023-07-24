package com.denchic45.studiversity.domain.model

import com.denchic45.stuiversity.api.course.model.CourseResponse
import java.util.UUID

data class CourseItem(
    val id: UUID,
    val name: String,
    val subject: SubjectItem?
)

fun CourseResponse.toItem() = CourseItem(
    id = id,
    name = name,
    subject = subject?.toItem()
)