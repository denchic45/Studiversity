package com.denchic45.studiversity.feature.course.material

import com.denchic45.studiversity.database.table.CourseElementDao
import com.denchic45.stuiversity.api.course.material.model.CourseMaterialResponse

fun CourseElementDao.toMaterialResponse(): CourseMaterialResponse {
    return CourseMaterialResponse(
        id = id.value,
        name = name,
        description = description,
        courseId = CourseElementDao.findById(id.value)!!.course.id.value,
        topicId = topic?.id?.value,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}