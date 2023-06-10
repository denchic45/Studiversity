package com.denchic45.studiversity.uivalidator

import com.denchic45.studiversity.uivalidator.rule.ErrorMessage

interface IRule<T> {
    fun validate(value: T, messageCallback: (message: ErrorMessage) -> Unit): Boolean

    fun isValid(value: T): Boolean
}