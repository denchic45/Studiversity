package com.denchic45.studiversity.domain.model

import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.timetable.model.StudyGroupNameResponse
import java.util.UUID

data class StudyGroupItem(val id: UUID, val name: String)

fun StudyGroupNameResponse.toItem() = StudyGroupItem(
    id = id,
    name = name
)

fun StudyGroupResponse.toItem() = StudyGroupItem(
    id = id,
    name = name
)