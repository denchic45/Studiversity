package com.denchic45.kts.data.mapper

import com.denchic45.kts.SubjectEntity
import com.denchic45.kts.data.remote.model.SubjectMap
import com.denchic45.kts.domain.model.Subject

fun SubjectEntity.entityToSubjectDomain() = Subject(
    id = subject_id,
    name = subject_name,
    iconUrl = icon_url,
    colorName = color_name
)

fun List<SubjectEntity>.entitiesToSubjectDomains() = map { it.entityToSubjectDomain() }

fun List<SubjectMap>.mapsToSubjectDomains() = map { it.mapToSubjectDomain() }

fun SubjectMap.mapToSubjectDomain() = Subject(
    id = id,
    name = name,
    iconUrl = iconUrl,
    colorName = colorName
)

fun SubjectMap.mapToSubjectEntity() = SubjectEntity(
    subject_id = id,
    subject_name = name,
    icon_url = iconUrl,
    color_name = colorName
)

fun List<SubjectMap>.mapsToSubjectEntities() = map { it.mapToSubjectEntity() }

fun Subject.domainToMap() = mapOf<String,Any>(
    "id" to id,
    "name" to name,
    "iconUrl" to iconUrl,
    "colorName" to colorName
)

fun List<Subject>.domainsToMaps() = map { it.domainToMap() }