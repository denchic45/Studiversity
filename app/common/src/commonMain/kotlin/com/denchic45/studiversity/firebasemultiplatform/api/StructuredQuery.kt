package com.denchic45.studiversity.firebasemultiplatform.api

import kotlinx.serialization.Serializable


@Serializable
data class StructuredQuery(
    val select: Projection? = null,
    val from: CollectionSelector,
    val where: Filter,
    val orderBy: Order? = null,
    val startAt: Cursor? = null,
    val endAt: Cursor? = null,
    val offset: Int? = null,
    val limit: Int? = null,
)
