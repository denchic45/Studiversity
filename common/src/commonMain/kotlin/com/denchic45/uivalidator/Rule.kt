package com.denchic45.uivalidator

import com.denchic45.uivalidator.rule.ErrorMessage

class Rule<T>(
    val predicate: (value: T) -> Boolean,
    val errorMessage: (value: T) -> ErrorMessage
) : IRule<T> {

    override fun validate(value: T, messageCallback: (message: ErrorMessage) -> Unit): Boolean {
        return isValid(value).apply { if (!this) messageCallback(errorMessage(value)) }
    }

    override fun isValid(value: T): Boolean = predicate(value)

}