package com.studiversity.feature.course.subject

import com.studiversity.database.table.SubjectDao
import com.stuiversity.api.course.subject.model.SubjectResponse
import org.jetbrains.exposed.sql.SizedIterable

fun SubjectDao.toResponse() = SubjectResponse(
    id = id.value,
    name = name,
    shortname = shortname,
    iconName = iconName
)

fun SizedIterable<SubjectDao>.toResponses() = map(SubjectDao::toResponse)