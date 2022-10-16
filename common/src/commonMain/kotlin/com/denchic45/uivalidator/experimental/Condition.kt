package com.denchic45.uivalidator.experimental

interface Condition<T> : Validatable {
    val onResult: ValidationResult?

    companion object {
        operator fun <T> invoke(
            value: () -> T,
            predicate: (value: T) -> Boolean,
            onResult: ValidationResult? = null,
        ): Condition<T> = DefaultCondition(value, predicate, onResult)
    }
}

private class DefaultCondition<T>(
    private val value: () -> T,
    private val predicate: (value: T) -> Boolean,
    override val onResult: ValidationResult? = null,
) : Condition<T> {

    override fun validate(): Boolean {
        return predicate(value()).apply { onResult?.invoke(this) }
    }
}

fun interface ValidationResult {
    operator fun invoke(isValid: Boolean)
}