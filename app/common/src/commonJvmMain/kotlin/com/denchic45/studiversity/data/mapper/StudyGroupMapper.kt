package com.denchic45.studiversity.data.mapper

import com.denchic45.studiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.studiversity.api.studygroup.model.AcademicYear
import com.denchic45.studiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.studiversity.entity.StudyGroup
import com.denchic45.studiversity.entity.StudyGroupWithSpecialty
import com.denchic45.studiversity.util.toUUID

fun StudyGroupWithSpecialty.toResponse() = StudyGroupResponse(
    id = study_group_id.toUUID(),
    name = group_name,
    academicYear = AcademicYear(start_academic_year, end_academic_year),
    specialty = SpecialtyResponse(
        specialty_id.toUUID(),
        specialty_name,
        shortname,
        code
    )
)

fun StudyGroupResponse.toEntity() = StudyGroup(
    study_group_id = id.toString(),
    group_name = name,
    specialty_id = specialty?.id.toString(),
    start_academic_year = academicYear.start,
    end_academic_year = academicYear.end
)