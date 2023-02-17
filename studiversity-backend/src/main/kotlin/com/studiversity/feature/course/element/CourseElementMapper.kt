package com.studiversity.feature.course.element

import com.studiversity.database.table.CourseElementDao
import com.studiversity.database.table.CourseElementDetailsDao
import com.studiversity.database.table.CourseElements
import com.studiversity.database.table.CourseWorkDao
import com.denchic45.stuiversity.api.course.element.model.CourseElementDetails
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.element.model.CourseWork
import org.jetbrains.exposed.sql.ResultRow

fun CourseElementDao.toResponse(detailsDao: CourseElementDetailsDao): CourseElementResponse = toResponse(
    detailsDao.toElementDetails()
)

private fun CourseElementDetailsDao.toElementDetails() =
    when (this) {
        is CourseWorkDao -> toDetailsResponse()
    }


private fun CourseElementDao.toResponse(details: CourseElementDetails): CourseElementResponse = CourseElementResponse(
    id = id.value,
    courseId = courseId,
    name = name,
    description = description,
    topicId = topicId,
    order = order,
    details = details
)

private fun CourseWorkDao.toDetailsResponse(): CourseElementDetails = CourseWork(
    dueDate = dueDate,
    dueTime = dueTime,
    workType = type,
    maxGrade = maxGrade,
    workDetails = null
)

fun ResultRow.toCourseElementResponse(detailsDao: CourseElementDetailsDao): CourseElementResponse =
    CourseElementResponse(
        id = this[CourseElements.id].value,
        courseId = this[CourseElements.courseId],
        name = this[CourseElements.name],
        description = this[CourseElements.description],
        topicId = this[CourseElements.topicId],
        order = this[CourseElements.order],
        details = detailsDao.toElementDetails()
    )
