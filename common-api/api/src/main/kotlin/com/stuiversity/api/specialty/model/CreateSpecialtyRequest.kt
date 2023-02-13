package com.stuiversity.api.specialty.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateSpecialtyRequest(
    val name: String,
    val shortname: String
)
