package com.denchic45.uivalidator.experimental

import com.denchic45.uivalidator.rule.ErrorMessage

class CombineCondition<T>(
    private val conditions: List<Validatable>,
    override val onResult: (isValid: Boolean, value: T?) -> Unit
) : ICondition<T?> {

    override fun validate(): Boolean {
        return conditions.all { validatable -> validatable.validate() }.apply { if (!this) onResult(this, validatable.) }
    }
}