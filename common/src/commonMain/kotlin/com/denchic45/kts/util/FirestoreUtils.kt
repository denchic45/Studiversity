package com.denchic45.kts.util

fun FireMap.timestampIsNull(): Boolean {
    return get("timestamp") == null
}

fun FireMap.timestampNotNull(): Boolean {
    return !timestampIsNull()
}

fun List<FireMap>.timestampsIsNull(): Boolean {
    return any { it.timestampIsNull() }
}

fun List<FireMap>.timestampsNotNull(): Boolean {
    return !timestampsIsNull()
}

typealias FireMap = Map<String, Any?>

typealias MutableFireMap = MutableMap<String, Any?>