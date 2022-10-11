package com.denchic45.uivalidator

import com.denchic45.uivalidator.rule.ErrorMessage

class Condition<T>(
    private val value: () -> T,
    private val rule: IRule<T>,
    private val onError: (errorMessage: ErrorMessage) -> Unit
) : Validatable {
    override fun isValid(): Boolean {
        return rule.isValid(value())
    }

    override fun validate(): Boolean {
        return rule.validate(value()) { onError(it) }
    }
}