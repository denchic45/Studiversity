package com.studiversity.feature.course.work

import com.denchic45.stuiversity.api.course.element.model.CourseElementDetails
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.element.model.CourseWork
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.studiversity.database.table.CourseElementDao
import com.studiversity.database.table.CourseWorkDao

fun CourseElementDao.toWorkResponse(courseWorkDao: CourseWorkDao): CourseWorkResponse {
    return CourseWorkResponse(
        id = id.value,
        name = name,
        description = description,
        dueDate = courseWorkDao.dueDate,
        dueTime = courseWorkDao.dueTime,
        workType = courseWorkDao.type,
        maxGrade = courseWorkDao.maxGrade,
        topicId = topic?.id?.value,
        submitAfterDueDate = courseWorkDao.submitAfterDueDate,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}