package com.denchic45.studiversity.uivalidator

import com.denchic45.studiversity.uivalidator.rule.ErrorMessage

class Condition<T>(
    private val value: () -> T,
    private val rule: com.denchic45.studiversity.uivalidator.IRule<T>,
    private val onError: (errorMessage: ErrorMessage) -> Unit
) : com.denchic45.studiversity.uivalidator.Validatable {
    override fun isValid(): Boolean {
        return rule.isValid(value())
    }

    override fun validate(): Boolean {
        return rule.validate(value()) { onError(it) }
    }
}