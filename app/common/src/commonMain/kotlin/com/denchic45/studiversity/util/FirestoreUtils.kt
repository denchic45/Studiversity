package com.denchic45.studiversity.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

typealias FireMap = Map<String, Any?>

typealias MutableFireMap = MutableMap<String, Any?>

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

fun LocalDateTime.toTimestampValue(): String = atOffset(ZoneOffset.UTC)
    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"))

fun LocalDate.toTimestampValue(): String = atStartOfDay().atOffset(ZoneOffset.UTC)
    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"))

fun Date.toTimestampValue(): String = toInstant().atOffset(ZoneOffset.UTC)
    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"))