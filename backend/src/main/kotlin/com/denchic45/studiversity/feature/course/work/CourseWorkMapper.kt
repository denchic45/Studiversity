package com.denchic45.studiversity.feature.course.work

import com.denchic45.studiversity.database.table.CourseElementDao
import com.denchic45.studiversity.database.table.CourseWorkDao
import com.denchic45.stuiversity.api.course.material.model.CourseMaterialResponse
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse

fun CourseElementDao.toWorkResponse(courseWorkDao: CourseWorkDao): CourseWorkResponse {
    return CourseWorkResponse(
        id = id.value,
        name = name,
        description = description,
        dueDate = courseWorkDao.dueDate,
        dueTime = courseWorkDao.dueTime,
        workType = courseWorkDao.type,
        maxGrade = courseWorkDao.maxGrade,
        courseId = CourseElementDao.findById(id.value)!!.course.id.value,
        topicId = topic?.id?.value,
        submitAfterDueDate = courseWorkDao.submitAfterDueDate,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}