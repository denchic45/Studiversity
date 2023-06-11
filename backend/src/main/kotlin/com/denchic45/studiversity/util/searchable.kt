package com.denchic45.studiversity.util

fun String.searchable() = trim().lowercase().replace("\\s+", "%")