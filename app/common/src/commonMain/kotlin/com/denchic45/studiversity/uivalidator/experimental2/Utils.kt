package com.denchic45.studiversity.uivalidator.experimental2

inline fun <R> getIf(b: Boolean?, block: () -> R): R? {
    return if (b == true) block() else null
}

inline fun <R> getIfNot(b: Boolean?, block: () -> R): R? {
    return if (b == false) block() else null
}