package com.denchic45.studiversity.data.mapper

import com.denchic45.studiversity.api.course.subject.model.SubjectResponse
import com.denchic45.studiversity.entity.Subject
import com.denchic45.studiversity.util.toUUID

fun Subject.toResponse() = SubjectResponse(
    id = subject_id.toUUID(),
    name = subject_name,
    shortname = shortname
)

fun List<Subject>.toSubjectResponses() = map { it.toResponse() }


fun SubjectResponse.toSubject() = Subject(
    subject_id = id.toString(),
    subject_name = name,
    shortname = shortname
)

fun List<SubjectResponse>.toSubjectEntities() = map { it.toSubject() }