package com.stuiversity.util

sealed class OptionalProperty<out T> {

    object NotPresent : OptionalProperty<Nothing>()

    data class Present<T>(val value: T) : OptionalProperty<T>()

    fun ifPresent(onPresent: (value: T) -> Unit) {
        if (this is Present) onPresent(value)
    }

    suspend fun ifPresentSuspended(onPresent: suspend (value: T) -> Unit) {
        if (this is Present) onPresent(value)
    }

    val isPresent: Boolean
        get() = this is Present

    companion object {
        fun <T> of(value: T) = Present(value)
    }
}

fun <T> OptionalProperty<T>.requirePresent(): T {
    return presentOrElse { throw IllegalStateException("Value not present") }
}

fun <T> OptionalProperty<T>.presentOrElse(defaultValue: () -> T) =
    (this as? OptionalProperty.Present)?.value ?: defaultValue()