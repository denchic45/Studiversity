package com.denchic45.kts.ui.model

import com.denchic45.stuiversity.api.course.model.CourseResponse
import java.util.*

data class GroupCourseItem(
    val id: UUID,
    val name: String,
    val iconName: String?,
)

fun CourseResponse.toGroupCourseItem() = GroupCourseItem(
    id = id,
    name = name,
    iconName = subject?.iconName
)