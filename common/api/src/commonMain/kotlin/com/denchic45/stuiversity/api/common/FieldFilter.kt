package com.denchic45.stuiversity.api.common

abstract class FieldFilter<T>(val field: String) {
    abstract val value: T
}

abstract class FieldFilterClass<T:FieldFilter<*>>(private vararg val factory: Pair<String, (value: String) -> T>) {
    fun create(field: String, value: String): T? {
        return factory.firstOrNull { it.first == field }?.run { second(value) }
    }
}