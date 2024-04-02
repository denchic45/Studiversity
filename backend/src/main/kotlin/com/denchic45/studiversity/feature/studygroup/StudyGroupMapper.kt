package com.denchic45.studiversity.feature.studygroup

import com.denchic45.studiversity.api.studygroup.model.AcademicYear
import com.denchic45.studiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.studiversity.database.table.StudyGroupDao
import com.denchic45.studiversity.feature.specialty.toResponse

fun StudyGroupDao.toResponse() = StudyGroupResponse(
    id = id.value,
    name = name,
    academicYear = AcademicYear(startAcademicYear, endAcademicYear),
    specialty = specialty?.toResponse()
)
