package com.studiversity.feature.course.work

import com.denchic45.stuiversity.api.course.work.grade.GradeResponse
import com.studiversity.database.table.GradeDao

fun GradeDao.toResponse() = GradeResponse(
    value = value,
    courseId = courseId,
    studentId = studentId,
    gradedBy = gradedBy,
    submissionId = submissionId?.value
)