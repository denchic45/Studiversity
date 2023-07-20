package com.denchic45.studiversity.data.mapper

import com.denchic45.studiversity.entity.Specialty
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.stuiversity.util.toUUID

fun Specialty.toResponse() = SpecialtyResponse(
    id = specialty_id.toUUID(),
    name = name,
    shortname = shortname
)

fun SpecialtyResponse.toEntity() = Specialty(
    specialty_id = id.toString(),
    name = name,
    shortname = shortname
)

fun List<SpecialtyResponse>.toSpecialtyEntities() = map(SpecialtyResponse::toEntity)