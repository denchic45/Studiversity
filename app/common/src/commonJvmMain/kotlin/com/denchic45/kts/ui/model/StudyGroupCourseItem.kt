package com.denchic45.kts.ui.model

import com.denchic45.stuiversity.api.course.model.CourseResponse
import java.util.*

data class StudyGroupCourseItem(
    val id: UUID,
    val name: String,
    val iconUrl: String?,
)

fun CourseResponse.toGroupCourseItem() = StudyGroupCourseItem(
    id = id,
    name = name,
    iconUrl = subject?.iconUrl
)