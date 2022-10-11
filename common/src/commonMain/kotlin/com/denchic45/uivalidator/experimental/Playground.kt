package com.denchic45.uivalidator.experimental

import com.denchic45.uivalidator.rule.ErrorMessage

class Playground {

    init {
        val condition1 = Condition(
            value = { "Ivan@gmail.com" },
            predicate = { it.contains("Ivan") },
            errorMessage = { ErrorMessage.StringMessage("$it no contains: Ivan") },
            onError = {
                when (it) {
                    is ErrorMessage.ResourceMessage -> TODO()
                    is ErrorMessage.StringMessage -> println(it.message)
                }
            }
        )
        val condition2 = Condition(
            value = { "Ivan" },
            predicate = { it.startsWith("1") },
            errorMessage = { ErrorMessage.StringMessage("$it not starts with 1") },
            onError = {
                when (it) {
                    is ErrorMessage.ResourceMessage -> TODO()
                    is ErrorMessage.StringMessage -> println(it.message)
                }
            }
        )
        CombineCondition<String>(
            conditions = listOf(
                condition1,
                condition2
            ))
    }
}