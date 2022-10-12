package com.denchic45.uivalidator.experimental

class Validator<T>(
    private vararg val conditions: Condition<T>,
    override val onResult: ((isValid: Boolean) -> Unit)?
) : ICondition<T> {

    override fun validate(): Boolean {
        return conditions.asList().allEach { it.validate() }.apply { onResult?.let { it(this) } }
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