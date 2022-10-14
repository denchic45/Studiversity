package com.denchic45.uivalidator.experimental

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface Condition<T> : Validatable {
    val onResult: ValidationResult?

    companion object {
        operator fun <T> invoke(
            value: () -> T,
            predicate: (value: T) -> Boolean,
            onResult: ValidationResult? = null
        ): Condition<T> = DefaultCondition(value, predicate, onResult)

        operator fun <T> invoke(
            value: () -> T,
            source: Flow<T>,
            coroutineScope: CoroutineScope,
            predicate: (value: T) -> Boolean,
            onResult: ValidationResult? = null
        ): Condition<T> = run {
            DefaultCondition(value, predicate, onResult).apply {
                coroutineScope.launch {
                    source.collect {
                        println("Validating...")
                        validate()
                    }
                }
            }
        }

//        operator fun <T> invoke(
//            value: () -> T,
//            predicate: List<(value: T) -> Boolean>,
//            onResult: ValidationResult? = null
//        ): Condition<T> = MultiCondition(value, predicate.toTypedArray(), onResult)
//
//        operator fun <T> invoke(
//            value: () -> T,
//            vararg predicate: (value: T) -> Boolean,
//            onResult: ValidationResult? = null
//        ): Condition<T> = MultiCondition(value, arrayOf(*predicate), onResult)
    }
}

private class DefaultCondition<T>(
    private val value: () -> T,
    private val predicate: (value: T) -> Boolean,
    override val onResult: ValidationResult? = null,
) : Condition<T> {

    override fun validate(): Boolean {
        return predicate(value()).apply { onResult?.invoke(this) }
    }
}

//private class MultiCondition<T>(
//    private val value: () -> T,
//    private val predicates: Array<(value: T) -> Boolean>,
//    override val onResult: ValidationResult?
//) : Condition<T> {
//    override fun validate(): Boolean {
//        return predicates.all { it(value()) }
//    }
//}

fun interface ValidationResult {
    operator fun invoke(isValid: Boolean)
}

class StateFlowResult<T>(
    private val stateFlow: MutableStateFlow<T?>,
    private val message: () -> T
) : ValidationResult {
    override fun invoke(isValid: Boolean) {
        stateFlow.update { if (isValid) null else message() }
    }
}
