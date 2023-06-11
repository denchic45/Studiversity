package com.denchic45.studiversity.validation

import io.ktor.server.plugins.requestvalidation.*

class ValidationResultBuilder {
    private val errors: MutableList<String> = mutableListOf()
    fun condition(condition: Boolean, errorMessage: String) {
        if (!condition) errors.add(errorMessage)
    }

    fun build(): ValidationResult {
        return if (errors.isEmpty())
            ValidationResult.Valid
        else ValidationResult.Invalid(errors)
    }

}

fun buildValidationResult(block: ValidationResultBuilder.() -> Unit): ValidationResult {
    val builder = ValidationResultBuilder().apply(block)
    return builder.build()
}