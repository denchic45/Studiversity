package com.denchic45.kts.util

fun FireMap.timestampIsNull(): Boolean {
    return get("timestamp") == null
}

fun FireMap.timestampNotNull(): Boolean {
    return !timestampIsNull()
}

fun List<Map<String, Any>>.timestampsIsNull(): Boolean {
    return any { it.timestampIsNull() }
}

fun List<Map<String, Any>>.timestampsNotNull(): Boolean {
    return !timestampsIsNull()
}

typealias FireMap = Map<String, Any>

typealias MutableFireMap = MutableMap<String, Any>