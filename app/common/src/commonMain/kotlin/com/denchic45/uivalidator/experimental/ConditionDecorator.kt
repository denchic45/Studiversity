package com.denchic45.uivalidator.experimental

import com.denchic45.uivalidator.Validatable

abstract class ConditionDecorator<T>(
    private val condition: Validatable,
) : Validatable {
    override fun isValid(): Boolean {
        return condition.isValid()
    }

    override fun validate(): Boolean {
        return condition.validate()
    }

}