package com.denchic45.uivalidator.experimental

class CombineCondition<out T>(
    private val conditions: List<ICondition<T>>,
    override val onResult: ((isValid: Boolean) -> Unit)? = null
) : ICondition<@UnsafeVariance T> {

    override fun validate(): Boolean {
        return conditions.allEach { condition -> condition.validate() }
            .apply { onResult?.invoke(this) }
    }
}