package com.denchic45.kts.ui.model

import com.denchic45.kts.domain.model.CourseHeader
import java.util.*

data class GroupCourseItem(
    val id: UUID,
    val name: String,
    val iconName: String,
)

fun CourseHeader.toGroupCourseItem() = GroupCourseItem(
    id = id,
    name = name,
    iconName = subject.iconName
)