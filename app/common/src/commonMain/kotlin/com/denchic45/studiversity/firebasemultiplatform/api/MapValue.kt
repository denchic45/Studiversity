package com.denchic45.studiversity.firebasemultiplatform.api

import kotlinx.serialization.Serializable

@Serializable
data class MapValue(val fields: Map<String, Value>)
