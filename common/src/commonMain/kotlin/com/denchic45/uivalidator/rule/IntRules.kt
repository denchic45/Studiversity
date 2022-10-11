package com.denchic45.uivalidator.rule

import com.denchic45.uivalidator.Rule
import com.denchic45.uivalidator.rule.ErrorMessage

fun NotZero(errorMessage: (value: Int) -> ErrorMessage): Rule<Int> {
    return Rule(
        predicate = { it != 0 },
        errorMessage = errorMessage
    )
}

