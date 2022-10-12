package com.denchic45.uivalidator.experimental

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

open class Condition<T>(
    val value: () -> T,
    val predicate: (value: T) -> Boolean,
    override val onResult: ((isValid: Boolean) -> Unit)? = null,
) : ICondition<T> {

    override fun validate(): Boolean {
        val value = value()
        return predicate(value).apply { onResult?.invoke(this) }
    }
}

interface ICondition<T> : Validatable {
    val onResult: ((isValid: Boolean) -> Unit)?
}

fun <T> stateFlowResult(
    stateFlow: MutableStateFlow<T?>,
    message: () -> T
): (isValid: Boolean) -> Unit = { isValid ->
    stateFlow.update {
        if (isValid) null else message()
    }
}