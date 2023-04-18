package com.denchic45.stuiversity.api.role.model

import kotlinx.serialization.Serializable

@Serializable
data class CheckCapabilitiesResponse(
    val permissions: Map<String, Boolean>
) {
    fun hasCapability(capability: Capability): Boolean {
        return permissions[capability.resource] ?: false
    }

    inline fun ifHasCapability(capability: Capability, action: () -> Unit) {
        if (hasCapability(capability)) {
            action()
        }
    }
}
