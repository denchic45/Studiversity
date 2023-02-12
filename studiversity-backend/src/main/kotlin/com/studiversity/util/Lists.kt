package com.studiversity.util

fun <T> List<T>.hasNotDuplicates(): Boolean {
    return size == toSet().size
}