package com.stuiversity.api.role.model

import kotlinx.serialization.Serializable

@Serializable
data class CheckCapabilitiesResponse(
    val permissions: Map<String, Boolean>
) {
    fun hasCapability(capability: Capability): Boolean {
        return permissions[capability.resource] ?: false
    }
}
