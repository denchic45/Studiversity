package com.denchic45.stuiversity.api.common

abstract class Sorting(
//    private val field: String,
) {
    abstract val order: SortOrder
//    override fun toString() = "$field:$order"
}

abstract class SortingClass<T : Sorting>(private vararg val factory: Pair<String, (sort: SortOrder) -> T>) {
    fun create(content: String): T? {
        val (field, sort) = content.split(":")
        return factory.firstOrNull { it.first == field }?.run { second(SortOrder.valueOf(sort)) }
    }
}

enum class SortOrder { ASC, DESC }