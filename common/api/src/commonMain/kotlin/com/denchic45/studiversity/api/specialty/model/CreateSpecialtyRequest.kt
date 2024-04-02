package com.denchic45.studiversity.api.specialty.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateSpecialtyRequest(
    val name: String,
    val shortname: String?,
    val code: String?
)
