package com.denchic45.studiversity.uivalidator.rule

import com.denchic45.studiversity.uivalidator.Rule

fun NotEmpty(
    errorMessage: (value: String) -> ErrorMessage
): com.denchic45.studiversity.uivalidator.Rule<String> =
    com.denchic45.studiversity.uivalidator.Rule(
        predicate = { it.isNotEmpty() },
        errorMessage = errorMessage
    )

fun lol() {
    NotEmpty { value -> ErrorMessage.StringMessage("") }
}