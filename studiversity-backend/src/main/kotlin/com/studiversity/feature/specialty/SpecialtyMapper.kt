package com.studiversity.feature.specialty

import com.studiversity.database.table.SpecialtyDao
import com.stuiversity.api.specialty.model.SpecialtyResponse

fun SpecialtyDao.toResponse(): SpecialtyResponse = SpecialtyResponse(
    id = id.value,
    name = name,
    shortname = shortname
)