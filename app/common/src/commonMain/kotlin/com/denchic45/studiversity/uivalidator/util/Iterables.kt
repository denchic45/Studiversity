package com.denchic45.studiversity.uivalidator.util

inline fun <T> Iterable<T>.anyEach(predicate: (T) -> Boolean): Boolean {
    var found = false
    for (element in this) if (predicate(element)) found = true
    return found
}

inline fun <T> Iterable<T>.allEach(predicate: (T) -> Boolean): Boolean {
    var notFound = true
    for (element in this) if (!predicate(element)) notFound = false
    return notFound
}