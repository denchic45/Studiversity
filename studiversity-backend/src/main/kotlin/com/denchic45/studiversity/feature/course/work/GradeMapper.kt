package com.denchic45.studiversity.feature.course.work

import com.denchic45.stuiversity.api.course.work.grade.GradeResponse
import com.denchic45.studiversity.database.table.GradeDao

fun GradeDao.toResponse() = GradeResponse(
    value = value,
    courseId = course.id.value,
    studentId = student.id.value,
    gradedBy = gradedBy?.id?.value,
    submissionId = submission?.id?.value
)