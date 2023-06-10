package com.denchic45.studiversity.uivalidator.experimental2

fun interface ValidationResult {
    operator fun invoke(isValid: Boolean)
}