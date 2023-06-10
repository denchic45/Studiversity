package com.denchic45.studiversity.firebasemultiplatform.api

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class Value @OptIn(ExperimentalSerializationApi::class) constructor(
    val booleanValue: Boolean? = null,
    val integerValue: String? = null,
    val doubleValue: Double? = null,
    val timestampValue: String? = null,
    val stringValue: String? = null,
    val bytesValue: String? = null,
    val referenceValue: String? = null,
    val geoPointValue: LatLng? = null,
    val mapValue: MapValue? = null,
    val arrayValue: ArrayValue? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val nullValue: Unit? = Unit,
) {
    @Serializable
    data class LatLng(val latitude: Int, val longitude: Int)
}