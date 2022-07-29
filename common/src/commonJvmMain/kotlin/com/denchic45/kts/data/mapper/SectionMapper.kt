package com.denchic45.kts.data.mapper

import com.denchic45.kts.SectionEntity
import com.denchic45.kts.data.remote.model.SectionMap
import com.denchic45.kts.domain.model.Section

fun SectionMap.mapToEntity() = SectionEntity(
    section_id = id,
    course_id = courseId,
    name = name,
    order = order
)

fun List<SectionMap>.mapsToEntities() = map { it.mapToEntity() }

fun SectionEntity.entityToUserDomain() = Section(
    courseId = course_id,
    name = name,
    order = order,
    id = section_id
)

fun List<SectionEntity>.entitiesToDomains() = map { it.entityToUserDomain() }