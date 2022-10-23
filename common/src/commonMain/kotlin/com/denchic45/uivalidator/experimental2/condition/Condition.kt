package com.denchic45.uivalidator.experimental2.condition

interface Condition<T> {
    fun validate(value: T): Boolean

    companion object {
        operator fun <T> invoke(predicate: (value: T) -> Boolean): Condition<T> {
            return DefaultCondition(predicate)
        }
    }
}

class DefaultCondition<T>(private val predicate: (value: T) -> Boolean) : Condition<T> {
    override fun validate(value: T): Boolean = predicate(value)
}