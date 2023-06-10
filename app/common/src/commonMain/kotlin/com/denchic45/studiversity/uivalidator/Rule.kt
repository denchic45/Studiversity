package com.denchic45.studiversity.uivalidator

import com.denchic45.studiversity.uivalidator.rule.ErrorMessage

class Rule<T>(
    val predicate: (value: T) -> Boolean,
    val errorMessage: (value: T) -> ErrorMessage
) : com.denchic45.studiversity.uivalidator.IRule<T> {

    override fun validate(value: T, messageCallback: (message: ErrorMessage) -> Unit): Boolean {
        return isValid(value).apply { if (!this) messageCallback(errorMessage(value)) }
    }

    override fun isValid(value: T): Boolean = predicate(value)

}