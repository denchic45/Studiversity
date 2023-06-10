package com.denchic45.studiversity.feature.course.subject

import com.denchic45.studiversity.database.table.SubjectDao
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import org.jetbrains.exposed.sql.SizedIterable

fun SubjectDao.toResponse() = SubjectResponse(
    id = id.value,
    name = name,
    shortname = shortname,
    iconUrl = iconUrl
)

fun SizedIterable<SubjectDao>.toResponses() = map(SubjectDao::toResponse)