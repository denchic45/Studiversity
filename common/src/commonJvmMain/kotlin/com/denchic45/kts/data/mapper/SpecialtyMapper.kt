package com.denchic45.kts.data.mapper

import com.denchic45.kts.SpecialtyEntity
import com.denchic45.kts.data.db.remote.model.SpecialtyMap
import com.denchic45.kts.data.remote.model.SpecialtyDoc
import com.denchic45.kts.domain.model.Specialty
import com.denchic45.kts.util.MutableFireMap
import com.denchic45.kts.util.SearchKeysGenerator

fun SpecialtyEntity.toDomain() = Specialty(
    id = specialty_id,
    name = name
)

fun SpecialtyDoc.toEntity() = SpecialtyEntity(
    specialty_id = id,
    name = name
)

fun SpecialtyMap.toDomain() = Specialty(
    id = id,
    name = name
)

fun List<SpecialtyMap>.mapsToDomains() = map { it.toDomain() }

fun SpecialtyMap.mapToSpecialtyEntity() = SpecialtyEntity(
    specialty_id = id,
    name = name
)

fun List<SpecialtyMap>.mapsToSpecialEntities() = map { it.mapToSpecialtyEntity() }

fun Specialty.toMap(): MutableFireMap = mutableMapOf(
    "id" to id,
    "name" to name,
    "searchKeys" to SearchKeysGenerator().generateKeys(name)
)

fun List<Specialty>.specialtiesToMaps() = map(Specialty::toMap)
