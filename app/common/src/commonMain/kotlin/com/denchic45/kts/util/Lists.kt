package com.denchic45.kts.util

fun <T> List<T>.swap(index1: Int, index2: Int): List<T> {
    return toMutableList().apply { swap(index1, index2) }
}

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}

inline fun <T> List<T>.copy(mutatorBlock: MutableList<T>.() -> Unit): List<T> {
    return toMutableList().apply(mutatorBlock)
}