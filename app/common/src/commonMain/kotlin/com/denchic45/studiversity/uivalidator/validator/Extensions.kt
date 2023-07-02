package com.denchic45.studiversity.uivalidator.validator

import com.denchic45.studiversity.uivalidator.ValidationResult

fun <T> Validator<T>.observable(result: ValidationResult): Validator<T> {
    return ObservableValidator(this, result)
}

class ObservableValidator<T>(
    private val validator: Validator<T>,
    private val onResult: ValidationResult
) : Validator<T> by validator {
    override fun validate(): Boolean {
        return validator.validate().apply { onResult(this) }
    }
}