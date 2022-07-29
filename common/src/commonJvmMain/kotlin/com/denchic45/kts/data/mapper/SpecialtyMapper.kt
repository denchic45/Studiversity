package com.denchic45.kts.data.mapper

import com.denchic45.kts.SpecialtyEntity
import com.denchic45.kts.data.remote.model.SpecialtyDoc
import com.denchic45.kts.data.remote.model.SpecialtyMap
import com.denchic45.kts.domain.model.Specialty

fun SpecialtyEntity.entityToUserDomain() = Specialty(
    id = specialty_id,
    name = name
)

fun SpecialtyDoc.docToEntity() = SpecialtyEntity(
    specialty_id = id,
    name = name
)

fun SpecialtyMap.mapToSpecialty() = Specialty(
    id = id,
    name = name
)

fun List<SpecialtyMap>.mapsToDomains() = map { it.mapToSpecialty() }

fun SpecialtyMap.mapToSpecialtyEntity() = SpecialtyEntity(
    specialty_id = id,
    name = name
)

fun List<SpecialtyMap>.mapsToSpecialEntities() = map { it.mapToSpecialtyEntity() }

