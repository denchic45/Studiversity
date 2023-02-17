package com.denchic45.stuiversity.api.role.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRolesRequest(val roleIds: List<Long>)
