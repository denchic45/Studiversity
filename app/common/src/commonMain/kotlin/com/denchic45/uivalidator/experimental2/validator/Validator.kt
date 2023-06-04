package com.denchic45.uivalidator.experimental2.validator

import com.denchic45.uivalidator.experimental2.Operator
import com.denchic45.uivalidator.experimental2.condition.Condition
import com.denchic45.uivalidator.experimental2.validate

interface Validator<T> {
    fun validate(): Boolean
}

class ValueValidator<T>(
    private val value: () -> T,
    private val conditions: List<Condition<T>>,
    private val operator: Operator<Condition<T>> = Operator.allEach()
) : Validator<T> {

    override fun validate(): Boolean {
        return value().run { conditions.validate(this, operator) }
    }
}

class CompositeValidator<T>(
    private val validators: List<Validator<out T>>,
    private val operator: Operator<Validator<out T>> = Operator.allEach()
) : Validator<T> {

    override fun validate(): Boolean = validators.validate(operator)

    inline fun onValid(block: () -> Unit) {
        if (validate()) block()
    }
}