package com.denchic45.studiversity.uivalidator.experimental

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

 class MultiCondition<T>(
    private val value: () -> T,
    conditions: List<Pair<(value: T) -> Boolean, ValidationResult?>>,
    val operator: Operator = Operator.All,
    override val onResult: ValidationResult? = null,
) : Condition<T> {

    private val conditions = conditions.map { Condition(value, it.first, it.second) }

    override fun validate(): Boolean {
        return operator(conditions).apply { onResult?.invoke(this) }
    }
}