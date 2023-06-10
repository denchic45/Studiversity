package com.denchic45.studiversity.firebasemultiplatform.api

import kotlinx.serialization.Serializable


@Serializable
data class Filter(
    val compositeFilter: CompositeFilter? = null,
    val fieldFilter: FieldFilter? = null,
    val unaryFilter: UnaryFilter? = null,
)

@Serializable
data class CompositeFilter(
    val op: Operator,
    val filters: List<Filter>,
) {
    enum class Operator { OPERATOR_UNSPECIFIED, AND }
}

@Serializable
data class FieldFilter(
    val field: FieldReference,
    val op: Operator,
    val value: Value,
) {
    enum class Operator {
        OPERATOR_UNSPECIFIED, LESS_THAN, LESS_THAN_OR_EQUAL, GREATER_THAN,
        GREATER_THAN_OR_EQUAL, EQUAL, NOT_EQUAL, ARRAY_CONTAINS, IN,
        ARRAY_CONTAINS_ANY, NOT_IN
    }
}

@Serializable
data class UnaryFilter(
    val op: Operator,
    val field: FieldReference,
) {
    enum class Operator { OPERATOR_UNSPECIFIED, IS_NAN, IS_NULL, IS_NOT_NAN, IS_NOT_NULL }
}