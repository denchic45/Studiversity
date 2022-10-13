package com.denchic45.uivalidator.experimental

class CombineCondition<T>(
    private val conditions: List<Condition<T>>,
    override val onResult: ValidationResult? = null
) : Condition<T> {

    override fun validate(): Boolean {
        return conditions.allEach { condition -> condition.validate() }
            .apply { onResult?.invoke(this) }
    }
}