package com.denchic45.uivalidator.experimental

import com.denchic45.uivalidator.Validatable
import com.denchic45.uivalidator.rule.ErrorMessage

interface ICondition<T> : Validatable {
    val value: () -> T
    val errorMessage: (value: T) -> ErrorMessage
    val onError: (errorMessage: ErrorMessage) -> Unit
}