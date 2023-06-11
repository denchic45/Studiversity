package com.denchic45.studiversity.util

fun <T> List<T>.hasNotDuplicates(): Boolean {
    return size == toSet().size
}