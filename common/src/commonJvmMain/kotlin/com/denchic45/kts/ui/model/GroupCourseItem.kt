package com.denchic45.kts.ui.model

import com.denchic45.kts.domain.model.CourseHeader

data class GroupCourseItem(
    val id: String,
    val name: String,
    val iconName: String,
    val teacherName: String,
    val teacherPhotoUrl: String,
)

fun CourseHeader.toGroupCourseItem() = GroupCourseItem(id = id,
    name = name,
    iconName = subject.iconName,
    teacherName = teacher.fullName,
    teacherPhotoUrl = teacher.photoUrl)