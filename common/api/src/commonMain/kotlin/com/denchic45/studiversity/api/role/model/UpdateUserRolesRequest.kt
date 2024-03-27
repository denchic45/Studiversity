package com.denchic45.studiversity.api.role.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRolesRequest(val roleIds: List<Long>)
