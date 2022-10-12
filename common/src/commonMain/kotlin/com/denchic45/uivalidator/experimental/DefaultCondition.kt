package com.denchic45.uivalidator.experimental

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

open class DefaultCondition<T>(
    val value: () -> T,
    val predicate: (value: T) -> Boolean,
    override val onResult: ((isValid: Boolean) -> Unit)? = null,
) : ICondition<T> {

    constructor(
        value: () -> T,
        vararg predicates: (value: T) -> Boolean,
        onResult: ((isValid: Boolean) -> Unit)?
    ) : this(value, { predicates.all { it(value()) } }, onResult) {
        DefaultCondition({ "" }, { true }, { true }, { true }) { isValid: Boolean -> }
    }

    override fun validate(): Boolean {
        val value = value()
        return predicate(value).apply { onResult?.invoke(this) }
    }
}

interface ICondition<T> : Validatable {
    val onResult: ((isValid: Boolean) -> Unit)?
}

@Suppress("FunctionName") // Factory function
fun <T> Condition(
    value: () -> T,
    predicate: (value: T) -> Boolean,
    onResult: ((isValid: Boolean) -> Unit)? = null
):ICondition<T> {
    return DefaultCondition()
}

fun <T> stateFlowResult(
    stateFlow: MutableStateFlow<T?>, message: () -> T
): (isValid: Boolean) -> Unit = { isValid ->
    stateFlow.update {
        if (isValid) null else message()
    }
}