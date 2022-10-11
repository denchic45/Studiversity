package com.denchic45.uivalidator.experimental

import com.denchic45.uivalidator.rule.ErrorMessage

open class Condition<T>(
    val value: () -> T,
    val predicate: (value: T) -> Boolean,
    val errorMessage: (value: T) -> ErrorMessage,
    override val onResult: ((isValid: Boolean, value: T) -> Unit)?,
) : ICondition<T> {

    override fun validate(): Boolean {
        val value = value()
        return predicate(value).apply { onResult?.invoke(this, value) }
    }
}

interface ICondition<T> : Validatable {
    val onResult: ((isValid: Boolean, value: T) -> Unit)?
}
