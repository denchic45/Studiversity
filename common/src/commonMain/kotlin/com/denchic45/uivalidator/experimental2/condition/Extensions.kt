package com.denchic45.uivalidator.experimental2.condition

import com.denchic45.uivalidator.experimental2.ValidationResult

fun <T> Condition<T>.observable(result: ValidationResult): Condition<T> {
    return ObservableCondition(this, result)
}

class ObservableCondition<T>(
    private val condition: Condition<T>, private val onResult: ValidationResult
) : Condition<T> by condition {
    override fun validate(value: T): Boolean {
        return condition.validate(value).apply { onResult(this) }
    }
}