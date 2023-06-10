package com.denchic45.studiversity.uivalidator.rule

import com.denchic45.studiversity.uivalidator.Rule
import com.denchic45.studiversity.uivalidator.rule.ErrorMessage

fun NotZero(errorMessage: (value: Int) -> ErrorMessage): com.denchic45.studiversity.uivalidator.Rule<Int> {
    return com.denchic45.studiversity.uivalidator.Rule(
        predicate = { it != 0 },
        errorMessage = errorMessage
    )
}

