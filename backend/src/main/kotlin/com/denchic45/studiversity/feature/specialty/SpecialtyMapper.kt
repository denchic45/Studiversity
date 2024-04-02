package com.denchic45.studiversity.feature.specialty

import com.denchic45.studiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.studiversity.database.table.SpecialtyDao

fun SpecialtyDao.toResponse() = SpecialtyResponse(
    id = id.value,
    name = name,
    shortname = shortname,
    code = code
)