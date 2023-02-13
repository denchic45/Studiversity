package com.denchic45.uivalidator.rule

import com.denchic45.uivalidator.IRule

class ConcatRule<T : Any>(private  val rules: List<IRule<T>>) : IRule<T> {

    override fun validate(value: T, messageCallback: (message: ErrorMessage) -> Unit): Boolean {
        return rules.all { rule -> rule.validate(value) { messageCallback(it) } }
    }

    override fun isValid(value: T): Boolean {
        return rules.all { rule -> rule.isValid(value) }
    }
}