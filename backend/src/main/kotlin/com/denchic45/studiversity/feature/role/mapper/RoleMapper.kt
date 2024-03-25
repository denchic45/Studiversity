package com.denchic45.studiversity.feature.role.mapper

import com.denchic45.studiversity.database.table.RoleDao
import com.denchic45.stuiversity.api.role.model.Role

fun RoleDao.toRole(): Role = Role(id = id.value, resource = shortname)