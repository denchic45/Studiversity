package com.denchic45.uivalidator.experimental

import com.denchic45.uivalidator.rule.ErrorMessage

open class Condition<T>(
    override val value: () -> T,
    val predicate: (value: T) -> Boolean,
    override val errorMessage: (value: T) -> ErrorMessage,
    override val onError: (errorMessage: ErrorMessage) -> Unit
) : ICondition<T> {

    override fun isValid(): Boolean {
        return predicate(value())
    }

    override fun validate(): Boolean {
        return isValid().apply { if (!this) onError(errorMessage(value())) }
    }
}