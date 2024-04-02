package com.denchic45.studiversity.data.mapper

import com.denchic45.studiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.studiversity.entity.Specialty


fun SpecialtyResponse.toEntity() = Specialty(
    specialty_id = id.toString(),
    name = name,
    shortname = shortname
)

fun List<SpecialtyResponse>.toSpecialtyEntities() = map(SpecialtyResponse::toEntity)