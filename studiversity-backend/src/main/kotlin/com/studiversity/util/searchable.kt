package com.studiversity.util

fun String.searchable() = trim().lowercase().replace("\\s+","%")