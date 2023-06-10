package com.denchic45.studiversity.uivalidator.rule

import com.denchic45.studiversity.uivalidator.IRule

class AnyRule<T : Any>(
    private vararg val rules: com.denchic45.studiversity.uivalidator.IRule<T>,
    private val message: () -> ErrorMessage
) : com.denchic45.studiversity.uivalidator.IRule<T> {

    override fun validate(value: T, messageCallback: (message: ErrorMessage) -> Unit): Boolean {
        return rules
            .any { rule -> rule.isValid(value) }
            .apply { if (!this) messageCallback(message()) }
    }

    override fun isValid(value: T): Boolean {
        return rules.any { rule -> rule.isValid(value) }
    }
}