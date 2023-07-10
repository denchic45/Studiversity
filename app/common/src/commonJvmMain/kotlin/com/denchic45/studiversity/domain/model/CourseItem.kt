package com.denchic45.studiversity.domain.model

import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import java.util.UUID

data class CourseItem(
    val id: UUID,
    val name: String,
    val subject: SubjectItem?
)

data class SubjectItem(
    val id: UUID,
    val name: String,
    val shortname: String,
    val iconUrl: String
)

fun CourseResponse.toItem() = CourseItem(
    id = id,
    name = name,
    subject = subject?.toItem()
)

fun SubjectResponse.toItem() = SubjectItem(
    id = id,
    name = name,
    shortname = shortname,
    iconUrl = iconUrl
)