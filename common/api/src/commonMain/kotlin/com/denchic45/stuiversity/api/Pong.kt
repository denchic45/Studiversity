package com.denchic45.stuiversity.api

import java.util.UUID

data class Pong(val organization: OrganizationResponse)

data class OrganizationResponse(
    val id: UUID,
    val name: String,
    val allowRegistration: Boolean
)