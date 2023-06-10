package com.denchic45.studiversity.uivalidator

class Validator(private vararg val conditions: com.denchic45.studiversity.uivalidator.Condition<out Any>) : Validatable {

    override fun validate(): Boolean {
        conditions.forEach(com.denchic45.studiversity.uivalidator.Condition<out Any>::validate)
        return isValid()
    }

    override fun isValid(): Boolean = conditions
        .all(com.denchic45.studiversity.uivalidator.Condition<out Any>::isValid)
}