package com.studiversity.feature.specialty

import com.studiversity.database.table.SpecialtyDao
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse

fun SpecialtyDao.toResponse(): SpecialtyResponse = SpecialtyResponse(
    id = id.value,
    name = name,
    shortname = shortname
)