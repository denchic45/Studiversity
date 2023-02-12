package com.denchic45.uivalidator.experimental2

fun interface ValidationResult {
    operator fun invoke(isValid: Boolean)
}