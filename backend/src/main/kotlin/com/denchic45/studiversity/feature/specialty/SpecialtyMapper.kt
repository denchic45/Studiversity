package com.denchic45.studiversity.feature.specialty

import com.denchic45.studiversity.database.table.SpecialtyDao
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse

fun SpecialtyDao.toResponse(): SpecialtyResponse = SpecialtyResponse(
    id = id.value,
    name = name,
    shortname = shortname
)