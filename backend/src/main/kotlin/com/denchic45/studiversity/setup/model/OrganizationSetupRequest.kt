package com.denchic45.studiversity.setup.model

import kotlinx.serialization.Serializable

@Serializable
data class OrganizationSetupRequest(
    val name: String,
    val selfRegistration: Boolean
)
