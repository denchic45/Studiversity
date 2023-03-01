package com.denchic45.kts.data.mapper

import com.denchic45.kts.SubjectEntity
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.util.toUUID

fun SubjectEntity.toResponse() = SubjectResponse(
    id = subject_id.toUUID(),
    name = subject_name,
    iconName = icon_name,
    shortname = subject_shortname
)

fun List<SubjectEntity>.toSubjectResponses() = map { it.toResponse() }


fun SubjectResponse.toSubjectEntity() = SubjectEntity(
    subject_id = id.toString(),
    subject_name = name,
    subject_shortname = shortname,
    icon_name = iconName
)

fun List<SubjectResponse>.toSubjectEntities() = map { it.toSubjectEntity() }