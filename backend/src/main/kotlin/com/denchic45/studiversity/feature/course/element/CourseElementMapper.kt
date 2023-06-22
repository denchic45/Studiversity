package com.denchic45.studiversity.feature.course.element

import com.denchic45.studiversity.database.table.CourseElementDao
import com.denchic45.studiversity.database.table.CourseElementDetailsDao
import com.denchic45.studiversity.database.table.CourseElements
import com.denchic45.studiversity.database.table.CourseWorkDao
import com.denchic45.stuiversity.api.course.element.model.CourseElementDetails
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.element.model.CourseWork
import org.jetbrains.exposed.sql.ResultRow

fun CourseElementDao.toResponse(detailsDao: CourseElementDetailsDao): CourseElementResponse = toResponse(
    detailsDao.toElementDetails()
)

fun CourseElementDetailsDao.toElementDetails() = when (this) {
    is CourseWorkDao -> toDetailsResponse()
}


fun CourseElementDao.toResponse(details: CourseElementDetails): CourseElementResponse = CourseElementResponse(
    id = id.value,
    courseId = course.id.value,
    name = name,
    description = description,
    topicId = topic?.id?.value,
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

fun ResultRow.toCourseElementResponse(details: CourseElementDetails): CourseElementResponse =
    CourseElementResponse(
        id = this[CourseElements.id].value,
        courseId = this[CourseElements.courseId].value,
        name = this[CourseElements.name],
        description = this[CourseElements.description],
        topicId = this[CourseElements.topicId]?.value,
        order = this[CourseElements.order],
        details = details
    )
