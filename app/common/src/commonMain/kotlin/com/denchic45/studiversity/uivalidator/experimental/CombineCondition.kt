package com.denchic45.studiversity.uivalidator.experimental

import com.denchic45.studiversity.uivalidator.util.allEach

class CombineCondition<T>(
    private val conditions: List<Condition<T>>,
    override val onResult: ValidationResult? = null
) : Condition<T> {

    override fun validate(): Boolean = conditions.allEach(Condition<T>::validate)
        .apply { onResult?.invoke(this) }
}