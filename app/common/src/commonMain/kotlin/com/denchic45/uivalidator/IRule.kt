package com.denchic45.uivalidator

import com.denchic45.uivalidator.rule.ErrorMessage

interface IRule<T> {
    fun validate(value: T, messageCallback: (message: ErrorMessage) -> Unit): Boolean

    fun isValid(value: T): Boolean
}