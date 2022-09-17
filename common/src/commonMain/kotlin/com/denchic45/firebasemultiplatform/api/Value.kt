package com.denchic45.firebasemultiplatform.api

import kotlinx.serialization.Serializable

@Serializable
data class Value(
    val booleanValue: Boolean? = null,
    val integerValue: String? = null,
    val doubleValue: Int? = null,
    val timestampValue: String? = null,
    val stringValue: String? = null,
    val bytesValue: String? = null,
    val referenceValue: String? = null,
    val geoPointValue: LatLng? = null,
    val mapValue: MapValue? = null,
) {
    @Serializable
    data class LatLng(val latitude: Int, val longitude: Int)

    @Serializable
    data class MapValue(val fields: Map<String, Value>)
}