package com.denchic45.studiversity.firebasemultiplatform.api

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val field: FieldReference,
    val direction: Direction
) {
    enum class Direction {DIRECTION_UNSPECIFIED, ASCENDING, DESCENDING}
}
