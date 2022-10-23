package com.denchic45.uivalidator.experimental


class Validator<T>(
    private val conditions: List<Condition<out T>>,
    val operator: Operator = Operator.AllEach,
    override val onResult: ValidationResult? = null,
) : Condition<T> {

    override fun validate(): Boolean {
        return operator(conditions).apply { onResult?.let { it(this) } }
    }
}