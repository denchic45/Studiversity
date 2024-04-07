package com.denchic45.studiversity.data.mapper

import com.denchic45.studiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.studiversity.entity.Specialty


fun SpecialtyResponse.toEntity() = Specialty(
    specialty_id = id.toString(),
    specialty_name = name,
    shortname = shortname,
    code = code
)

fun List<SpecialtyResponse>.toSpecialtyEntities() = map(SpecialtyResponse::toEntity)