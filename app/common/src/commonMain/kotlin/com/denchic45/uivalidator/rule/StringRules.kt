package com.denchic45.uivalidator.rule

import com.denchic45.uivalidator.Rule

fun NotEmpty(
    errorMessage: (value: String) -> ErrorMessage
): Rule<String> = Rule(
    predicate = { it.isNotEmpty() },
    errorMessage = errorMessage
)

fun lol() {
    NotEmpty { value -> ErrorMessage.StringMessage("") }
}