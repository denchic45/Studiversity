package com.denchic45.studiversity.feature.studygroup.mapper

import com.denchic45.stuiversity.api.studygroup.model.AcademicYear
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.studiversity.database.table.StudyGroupDao

//fun StudyGroupDao.toStudyGroup() = StudyGroup(
//    id = id.value,
//    name = name,
//    academicYear = StudyGroup.AcademicYear(Year.of(academicYear[0]), Year.of(academicYear[1])),
//    specialty = specialty?.toSpecialty()
//)

fun StudyGroupDao.toResponse() = StudyGroupResponse(
    id = id.value,
    name = name,
    academicYear = AcademicYear(startAcademicYear, endAcademicYear),
    specialty = specialty?.toResponse()
)
