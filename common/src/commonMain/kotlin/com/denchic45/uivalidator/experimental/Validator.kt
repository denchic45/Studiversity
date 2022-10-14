package com.denchic45.uivalidator.experimental

import kotlinx.coroutines.flow.StateFlow

class Validator<T>(
    private val conditions: List<Condition<T>>,
    val operator: Operator = Operator.AllEach,
    override val onResult: ValidationResult? = null,
) : Condition<T> {

    override fun validate(): Boolean {
        return operator(conditions).apply { onResult?.let { it(this) } }
    }
}

inline fun <T> Iterable<T>.anyEach(predicate: (T) -> Boolean): Boolean {
    var found = false
    for (element in this) if (predicate(element)) found = true
    return found
}

inline fun <T> Iterable<T>.allEach(predicate: (T) -> Boolean): Boolean {
    var notFound = true
    for (element in this) if (predicate(element)) notFound = false
    return notFound
}

fun interface Operator {
    operator fun invoke(conditions: List<Condition<*>>): Boolean

    companion object {

        val AllEach: Operator = Operator { it.allEach(Condition<*>::validate) }

        val AnyEach: Operator = Operator { it.anyEach(Condition<*>::validate) }

        val All: Operator = Operator { it.all(Condition<*>::validate) }

        val Any: Operator = Operator { it.any(Condition<*>::validate) }
    }
}

//class AnyEach : Operator {
//    override fun invoke(conditions: List<Condition<*>>): Boolean {
//        return conditions.anyEach(Condition<*>::validate)
//    }
//}
//
//class AllEach : Operator {
//    override fun invoke(conditions: List<Condition<*>>): Boolean {
//        return conditions.allEach(Condition<*>::validate)
//    }
//}