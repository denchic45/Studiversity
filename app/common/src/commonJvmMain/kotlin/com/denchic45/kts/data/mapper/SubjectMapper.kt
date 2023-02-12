package com.denchic45.kts.data.mapper

import com.denchic45.kts.SubjectEntity
import com.denchic45.kts.data.db.remote.model.SubjectMap
import com.denchic45.kts.domain.model.Subject
import com.denchic45.kts.util.SearchKeysGenerator

fun SubjectEntity.entityToSubjectDomain() = Subject(
    id = subject_id,
    name = subject_name,
    iconName = icon_name,
)

fun List<SubjectEntity>.entitiesToSubjectDomains() = map { it.entityToSubjectDomain() }

fun List<SubjectMap>.mapsToSubjectDomains() = map { it.mapToSubjectDomain() }

fun SubjectMap.mapToSubjectDomain() = Subject(
    id = id,
    name = name,
    iconName = iconName
)

fun SubjectMap.mapToSubjectEntity() = SubjectEntity(
    subject_id = id,
    subject_name = name,
    icon_name = iconName
)

fun List<SubjectMap>.mapsToSubjectEntities() = map { it.mapToSubjectEntity() }

fun Subject.domainToMap() = mapOf(
    "id" to id,
    "name" to name,
    "iconName" to iconName,
    "searchKeys" to SearchKeysGenerator().generateKeys(name)
)

fun List<Subject>.domainsToMaps() = map { it.domainToMap() }