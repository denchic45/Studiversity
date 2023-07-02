package com.denchic45.studiversity.uivalidator

fun interface ValidationResult {
    operator fun invoke(isValid: Boolean)
}